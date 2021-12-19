package com.qjx.qmall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.exception.NoStockException;
import com.qjx.qmall.common.to.mq.OrderTo;
import com.qjx.qmall.common.to.mq.SeckillOrderTo;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.common.vo.MemberEntityWithSocialVo;
import com.qjx.qmall.order.constant.OrderConstant;
import com.qjx.qmall.order.dao.OrderDao;
import com.qjx.qmall.order.entity.OrderEntity;
import com.qjx.qmall.order.entity.OrderItemEntity;
import com.qjx.qmall.order.entity.PaymentInfoEntity;
import com.qjx.qmall.order.enume.OrderStatusEnum;
import com.qjx.qmall.order.feign.CartFeignService;
import com.qjx.qmall.order.feign.MemberFeignService;
import com.qjx.qmall.order.feign.ProductFeignService;
import com.qjx.qmall.order.feign.WmsFeignService;
import com.qjx.qmall.order.interceptor.LoginUserInterceptor;
import com.qjx.qmall.order.service.OrderItemService;
import com.qjx.qmall.order.service.OrderService;
import com.qjx.qmall.order.service.PaymentInfoService;
import com.qjx.qmall.order.to.OrderCreateTo;
import com.qjx.qmall.order.vo.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {


	private ThreadLocal<OrderSubmitVo> orderSubmitVoTl = new ThreadLocal<>();

	@Resource
	MemberFeignService memberFeignService;

	@Resource
	CartFeignService cartFeignService;

	@Resource
	WmsFeignService wmsFeignService;

	@Resource
	ProductFeignService productFeignService;

	@Resource
	ThreadPoolExecutor executor;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	OrderItemService orderItemService;

	@Resource
	RabbitTemplate rabbitTemplate;

	@Resource
	PaymentInfoService paymentInfoService;


    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderEntity> page = this.page(
                new Query<OrderEntity>().getPage(params),
                new QueryWrapper<OrderEntity>()
        );

        return new PageUtils(page);
    }

	@Override
	public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {
		OrderConfirmVo confirmVo = new OrderConfirmVo();
		MemberEntityWithSocialVo memberEntityWithSocialVo = LoginUserInterceptor.loginUser.get();

		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();


		//1远程查询所有的收货地址列表
		CompletableFuture<Void> getAddressFutures = CompletableFuture.runAsync(() -> {
			//给新线程添加旧线程数据
			RequestContextHolder.setRequestAttributes(requestAttributes);
			List<MemberAddressVo> address = memberFeignService.getAddress(memberEntityWithSocialVo.getId());
			confirmVo.setAddress(address);
		}, executor);


		//2远程查询购物车所有选中的购物项
		CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
			RequestContextHolder.setRequestAttributes(requestAttributes);
			List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
			confirmVo.setItems(currentUserCartItems);
		}, executor).thenRunAsync(() -> {
			List<OrderItemVo> items = confirmVo.getItems();
			if (items != null && items.size() > 0) {
				List<Long> collect = items.stream().map(item -> {
					return item.getSkuId();
				}).collect(Collectors.toList());
				R r = wmsFeignService.getSkusHasStock(collect);
				List<SkuHasStockVo> data = r.getData(new TypeReference<List<SkuHasStockVo>>() {
				});
				if (data != null && data.size() > 0) {
					Map<Long, Boolean> map = data.stream().collect(Collectors.toMap(SkuHasStockVo::getSkuId, SkuHasStockVo::getHasStock));
					confirmVo.setStocks(map);
				}
			}

		},executor);


		//3.查询用户积分
		Integer integration = memberEntityWithSocialVo.getIntegration();
		confirmVo.setIntegration(integration);

		//4. 计算其他数据

		//5.防重令牌
		String token = UUID.randomUUID().toString().replace("-", "");


		confirmVo.setOrderToken(token);
		stringRedisTemplate.opsForValue()
				.set(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntityWithSocialVo.getId(),token, 30, TimeUnit.MINUTES);

		CompletableFuture.allOf(getAddressFutures, cartFuture).get();
		return confirmVo;
	}

//	@GlobalTransactional//高并发不适用seata, 选择用RabbitMQ 延时队列解决最终一致性问题
	@Transactional
	@Override
	public SubmitOrderResponseVo submitOrder(OrderSubmitVo vo) {
		orderSubmitVoTl.set(vo);
    	SubmitOrderResponseVo response = new SubmitOrderResponseVo();

		MemberEntityWithSocialVo memberEntityWithSocialVo = LoginUserInterceptor.loginUser.get();
		response.setCode(0);

		//1. 验证令牌 0 失败 1 成功
		String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
		String orderToken = vo.getOrderToken();
		Long result = stringRedisTemplate.execute(new DefaultRedisScript<Long>(script, Long.class),
				Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntityWithSocialVo.getId()),
				orderToken);
		if (result == 0L) {
			//验证失败
			response.setCode(1);
			return response;
		} else {
			//验证成功
			//去创建订单,验令牌,验价格,锁库存...
			OrderCreateTo order = createOrder();
			BigDecimal payAmount = order.getOrder().getPayAmount();
			BigDecimal payPrice = vo.getPayPrice();
			if (Math.abs(payAmount.subtract(payPrice).doubleValue()) < 0.01) {

				//保存订单
				saveOrder(order);
				//库存锁定,只要有异常,回滚
				//订单号,订单项信息(skuId, num, skuName)
				WareSkuLockVo lockVo = new WareSkuLockVo();
				lockVo.setOrderSn(order.getOrder().getOrderSn());
				List<OrderItemVo> collect = order.getOrderItems().stream().map(item -> {
					OrderItemVo orderItemVo = new OrderItemVo();
					orderItemVo.setSkuId(item.getSkuId());
					orderItemVo.setCount(item.getSkuQuantity());
					orderItemVo.setTitle(item.getSkuName());
					return orderItemVo;
				}).collect(Collectors.toList());
				lockVo.setLocks(collect);
				//远程锁库存
				//为了保证高并发, 发消息给库存服务
				//或者库存服务自动解锁模式,消息队列
				R r = wmsFeignService.orderLockStock(lockVo);
				if (r.getCode() == 0) {
					//锁定成功
					response.setOrder(order.getOrder());

//					int i = 10/0;
					//订单创建成功,给mq发送消息
					rabbitTemplate.convertAndSend("order-event-exchange",
							"order.create.order",
							order.getOrder());

					return response;
				} else {
					//锁定失败
					String msg = (String) r.get("msg");
					throw new NoStockException(msg);
//					response.setCode(3);
				}



			} else {
				response.setCode(2);
				return response;
			}
		}

//		String redisToken = stringRedisTemplate.opsForValue().get(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntityWithSocialVo.getId());

//		if (orderToken != null && orderToken.equals(redisToken)) {
//			//令牌验证通过
//			stringRedisTemplate.delete(OrderConstant.USER_ORDER_TOKEN_PREFIX + memberEntityWithSocialVo.getId());
//		} else {
//
//		}

	}

	@Override
	public OrderEntity getOrderByOrderSn(String orderSn) {
		OrderEntity orderEntity = getOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));
		return orderEntity;
	}

	@Override
	public void closeOrder(OrderEntity entity) {
		OrderEntity orderEntity = getById(entity.getId());
		if (orderEntity.getStatus() == OrderStatusEnum.CREATE_NEW.getCode()) {
			//关单
			OrderEntity update = new OrderEntity();
			update.setId(entity.getId());
			update.setStatus(OrderStatusEnum.CANCLED.getCode());
			updateById(update);

			OrderTo orderTo = new OrderTo();
			BeanUtils.copyProperties(orderEntity,orderTo);
			//发给mq一个释放库存消息
			try {
				rabbitTemplate.convertAndSend("order-event-exchange",
						"order.release.other",
						orderTo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public PayVo getOrderPay(String orderSn) {
		PayVo payVo = new PayVo();
		OrderEntity orderEntity = getOrderByOrderSn(orderSn);
		BigDecimal bigDecimal = orderEntity.getPayAmount().setScale(2, BigDecimal.ROUND_UP);

		payVo.setTotal_amount(bigDecimal.toString());

		payVo.setOut_trade_no(orderSn);

		List<OrderItemEntity> orderItem = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", orderSn));


		payVo.setSubject(orderItem.get(0).getSkuName());

		payVo.setBody(orderItem.get(0).getSkuAttrsVals());

		return payVo;


	}

	@Override
	public PageUtils queryPageWithItem(Map<String, Object> params) {
		MemberEntityWithSocialVo memberEntityWithSocialVo = LoginUserInterceptor.loginUser.get();
		IPage<OrderEntity> page = this.page(
				new Query<OrderEntity>().getPage(params),
				new QueryWrapper<OrderEntity>().eq("member_id", memberEntityWithSocialVo.getId()).orderByDesc("id")
		);
		List<OrderEntity> order_sn = page.getRecords().stream().peek(order -> {
			List<OrderItemEntity> orderItems = orderItemService.list(new QueryWrapper<OrderItemEntity>().eq("order_sn", order.getOrderSn()));
			order.setItemEntities(orderItems);
		}).collect(Collectors.toList());
		page.setRecords(order_sn);

		return new PageUtils(page);
	}

	//处理支付宝的支付结果
	@Override
	public String handlePayResult(PayAsyncVo vo) {
    	//保存交易流水
		PaymentInfoEntity paymentInfoEntity = new PaymentInfoEntity();
		paymentInfoEntity.setAlipayTradeNo(vo.getTrade_no());
		paymentInfoEntity.setOrderSn(vo.getOut_trade_no());
		paymentInfoEntity.setPaymentStatus(vo.getTrade_status());
		paymentInfoEntity.setCallbackTime(vo.getNotify_time());
		paymentInfoService.save(paymentInfoEntity);

		//修改订单状态信息
		if (vo.getTrade_status().equals("TRADE_SUCCESS") || vo.getTrade_status().equals("TRADE_FINISH")) {
			String outTradeNo = vo.getOut_trade_no();
			baseMapper.updateOrderStatus(outTradeNo, OrderStatusEnum.PAYED.getCode());
		}


		return null;
	}

	@Override
	public void createSeckillOrder(SeckillOrderTo seckillOrderTo) {
		OrderEntity orderEntity = new OrderEntity();
		orderEntity.setOrderSn(seckillOrderTo.getOrderSn());
		orderEntity.setMemberId(seckillOrderTo.getMemberId());

		orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());

		BigDecimal payAmount = seckillOrderTo.getSeckillPrice().multiply(new BigDecimal(seckillOrderTo.getNum().toString()));
		orderEntity.setPayAmount(payAmount);

		save(orderEntity);
		//保存订单项信息
		OrderItemEntity orderItemEntity = new OrderItemEntity();
		orderItemEntity.setOrderSn(seckillOrderTo.getOrderSn());
		orderItemEntity.setRealAmount(payAmount);
		orderItemEntity.setSkuQuantity(seckillOrderTo.getNum());
		//获取当前sku详细信息
//		productFeignService.getSpuInfoBySkuId()
		orderItemService.save(orderItemEntity);
	}


	//保存订单数据
	private void saveOrder(OrderCreateTo order) {
		OrderEntity orderEntity = order.getOrder();
		orderEntity.setModifyTime(new Date());
		this.save(orderEntity);

		List<OrderItemEntity> orderItems = order.getOrderItems();
		orderItemService.saveBatch(orderItems);


	}

	private OrderCreateTo createOrder() {
		OrderCreateTo to = new OrderCreateTo();
		//生成订单号
		String orderSn = IdWorker.getTimeId();

		OrderEntity orderEntity = buildOrder(orderSn);

		//获取到所有订单项
		List<OrderItemEntity> orderItemEntities = buildOrderItems(orderSn);

		//验价//计算价格相关
		computePrice(orderEntity, orderItemEntities);


		to.setOrder(orderEntity);
		to.setOrderItems(orderItemEntities);

		return to;
	}

	private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItemEntities) {
    	//1.订单价格相关的数据
		BigDecimal total = new BigDecimal("0.0");
		BigDecimal coupon = new BigDecimal("0.0");
		BigDecimal integration = new BigDecimal("0.0");
		BigDecimal promotion = new BigDecimal("0.0");
		Integer gift = 0;
		Integer growth = 0;

		for (OrderItemEntity orderItemEntity : orderItemEntities) {
			coupon = coupon.add(orderItemEntity.getCouponAmount());
			integration = integration.add(orderItemEntity.getIntegrationAmount());
			promotion = promotion.add(orderItemEntity.getPromotionAmount());
			total = total.add(orderItemEntity.getRealAmount());
			growth += orderItemEntity.getGiftGrowth();
			gift += orderItemEntity.getGiftIntegration();
		}
		orderEntity.setTotalAmount(total);
		orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));

		orderEntity.setPromotionAmount(promotion);
		orderEntity.setCouponAmount(coupon);
		orderEntity.setIntegrationAmount(integration);

		//设置积分等信息
		orderEntity.setIntegration(gift);
		orderEntity.setGrowth(growth);

		orderEntity.setDeleteStatus(0); //未删除
	}

	private OrderEntity buildOrder(String orderSn) {
		MemberEntityWithSocialVo respVo = LoginUserInterceptor.loginUser.get();
		OrderEntity orderEntity = new OrderEntity();

		orderEntity.setOrderSn(orderSn);

		orderEntity.setMemberId(respVo.getId());
		orderEntity.setMemberUsername(respVo.getUsername());


		OrderSubmitVo orderSubmitVo = orderSubmitVoTl.get();
		//获取收货地址信息
		R fare = wmsFeignService.getFare(orderSubmitVo.getAddrId());
		FareVo fareResp = fare.getData(new TypeReference<FareVo>() {
		});
		//设置运费信息
		orderEntity.setFreightAmount(fareResp.getFare());
		//设置收货人信息
		orderEntity.setReceiverCity(fareResp.getAddress().getCity());
		orderEntity.setReceiverDetailAddress(fareResp.getAddress().getDetailAddress());
		orderEntity.setReceiverName(fareResp.getAddress().getName());
		orderEntity.setReceiverPhone(fareResp.getAddress().getPhone());
		orderEntity.setReceiverPostCode(fareResp.getAddress().getPostCode());
		orderEntity.setReceiverProvince(fareResp.getAddress().getProvince());
		orderEntity.setReceiverRegion(fareResp.getAddress().getRegion());


		orderEntity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
		orderEntity.setConfirmStatus(7);

		return orderEntity;
	}

	//构建所有订单项数据
	private List<OrderItemEntity> buildOrderItems(String orderSn) {
    	//构建订单项数据
		//2.获取到所有的订单项
		List<OrderItemVo> currentUserCartItems = cartFeignService.getCurrentUserCartItems();
		if (currentUserCartItems != null && currentUserCartItems.size() > 0) {
			List<OrderItemEntity> orderItems = currentUserCartItems.stream().map(item -> {
				OrderItemEntity orderItemEntity = buildOrderItem(item);
				orderItemEntity.setOrderSn(orderSn);

				return orderItemEntity;
			}).collect(Collectors.toList());
			return orderItems;
		}
		return null;

	}

	private OrderItemEntity buildOrderItem (OrderItemVo cartItem) {
		OrderItemEntity itemEntity = new OrderItemEntity();
		//1. 订单信息:订单号
		//2. 商品spu信息
		Long skuId = cartItem.getSkuId();
		R r = productFeignService.getSpuInfoBySkuId(skuId);
		SpuInfoVo spuInfoVo = r.getData(new TypeReference<SpuInfoVo>() {
		});
		itemEntity.setSpuId(spuInfoVo.getId());
		itemEntity.setSpuBrand(spuInfoVo.getBrandId().toString());
		itemEntity.setSpuName(spuInfoVo.getSpuName());
		itemEntity.setCategoryId(spuInfoVo.getCatalogId());
		//3. 商品sku信息
		itemEntity.setSkuId(cartItem.getSkuId());
		itemEntity.setSkuName(cartItem.getTitle());
		itemEntity.setSkuPic(cartItem.getImage());
		itemEntity.setSkuPrice(cartItem.getPrice());
		String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
		itemEntity.setSkuAttrsVals(skuAttr);
		itemEntity.setSkuQuantity(cartItem.getCount());
		//4. 优惠信息
		//5. 积分信息
		itemEntity.setGiftGrowth(cartItem.getPrice().intValue() * cartItem.getCount());
		itemEntity.setGiftIntegration(cartItem.getPrice().intValue() * cartItem.getCount());
		//6.订单项的价格信息
		itemEntity.setPromotionAmount(new BigDecimal("0.0"));
		itemEntity.setCouponAmount(new BigDecimal("0.0"));
		itemEntity.setIntegrationAmount(new BigDecimal("0.0"));
		//当前订单项的实际金额
		BigDecimal orignal = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
		BigDecimal subtract = orignal.subtract(itemEntity.getCouponAmount()
				.subtract(itemEntity.getCouponAmount()
						.subtract(itemEntity.getIntegrationAmount())));
		itemEntity.setRealAmount(subtract);
    	return itemEntity;
	}

}
