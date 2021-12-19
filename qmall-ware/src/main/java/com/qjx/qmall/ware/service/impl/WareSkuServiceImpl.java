package com.qjx.qmall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.exception.NoStockException;
import com.qjx.qmall.common.to.mq.OrderTo;
import com.qjx.qmall.common.to.mq.StockDetailTo;
import com.qjx.qmall.common.to.mq.StockLockedTo;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.ware.dao.WareSkuDao;
import com.qjx.qmall.ware.entity.WareOrderTaskDetailEntity;
import com.qjx.qmall.ware.entity.WareOrderTaskEntity;
import com.qjx.qmall.ware.entity.WareSkuEntity;
import com.qjx.qmall.ware.feign.OrderFeignService;
import com.qjx.qmall.ware.feign.ProductFeignService;
import com.qjx.qmall.ware.service.WareOrderTaskDetailService;
import com.qjx.qmall.ware.service.WareOrderTaskService;
import com.qjx.qmall.ware.service.WareSkuService;
import com.qjx.qmall.ware.vo.OrderItemVo;
import com.qjx.qmall.ware.vo.OrderVo;
import com.qjx.qmall.ware.vo.SkuHasStockVo;
import com.qjx.qmall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RabbitListener(queues = "stock.release.stock.queue")
@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

	@Autowired
	ProductFeignService productFeignService;

	@Resource
	RabbitTemplate rabbitTemplate;

	@Resource
	WareOrderTaskService wareOrderTaskService;

	@Resource
	WareOrderTaskDetailService wareOrderTaskDetailService;

	@Resource
	OrderFeignService orderFeignService;




	private void unlockStock(Long skuId, Long wareId, Integer num, Long taskDetailId) {
		//库存解锁
		baseMapper.unlockStock(skuId, wareId, num);
		//更新库存工作单的状态
		WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity();
		detailEntity.setId(taskDetailId);
		detailEntity.setLockStatus(2);
		wareOrderTaskDetailService.updateById(detailEntity);

	}


	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
		String skuId = (String) params.get("skuId");
		if (!StringUtils.isEmpty(skuId)) {
			wrapper.eq("sku_id", skuId);
		}

		String wareId = (String) params.get("wareId");
		if (!StringUtils.isEmpty(wareId)) {
			wrapper.eq("ware_id", wareId);
		}

		IPage<WareSkuEntity> page = this.page(
				new Query<WareSkuEntity>().getPage(params),
				wrapper
		);

		return new PageUtils(page);
	}

	@Override
	public void addStore(Long skuId, Long wareId, Integer skuNum) {
		List<WareSkuEntity> entities = this.baseMapper.selectList(new QueryWrapper<WareSkuEntity>()
				.eq("sku_id", skuId).eq("ware_id", wareId));
		if (entities == null || entities.size() == 0) {
			WareSkuEntity wareSkuEntity = new WareSkuEntity();
			wareSkuEntity.setSkuId(skuId);
			wareSkuEntity.setWareId(wareId);
			wareSkuEntity.setStock(skuNum);
			wareSkuEntity.setStockLocked(0);


			try {
				R info = productFeignService.info(skuId);
				if (info.getCode() == 0) {
					Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");
					wareSkuEntity.setSkuName((String) data.get("skuName"));
				}
			} catch (Exception e) {

			}


			this.baseMapper.insert(wareSkuEntity);
		} else {

			this.baseMapper.addStock(skuId, wareId, skuNum);
		}


	}

	@Override
	public List<SkuHasStockVo> getSkusHasStock(List<Long> skuIds) {

		List<SkuHasStockVo> collect = skuIds.stream().map(skuId -> {
			SkuHasStockVo vo = new SkuHasStockVo();

			//查询sku总库存量
			Long count = baseMapper.getSkuStock(skuId);
			vo.setSkuId(skuId);
			vo.setHasStock(count != null && count > 0);
			return vo;
		}).collect(Collectors.toList());
		return collect;
	}

	@Transactional
	@Override
	public Boolean orderLockStock(WareSkuLockVo vo) {
		//保存库存工作单的详情
		WareOrderTaskEntity wareOrderTaskEntity = new WareOrderTaskEntity();
		wareOrderTaskEntity.setOrderSn(vo.getOrderSn());
		wareOrderTaskService.save(wareOrderTaskEntity);

		//按照下单的收货地址,找到一个就近仓库,锁定库存
		//找到每个商品在哪个仓库有库存
		List<OrderItemVo> locks = vo.getLocks();

		List<SkuWareHasStock> collect = locks.stream().map(item -> {
			Long skuId = item.getSkuId();
			SkuWareHasStock stock = new SkuWareHasStock();
			stock.setSkuId(skuId);
			stock.setCount(item.getCount());
			//查询这个商品在哪里有库存
			List<Long> wareIds = listWareIdHasStock(skuId);
			stock.setWareId(wareIds);

			return stock;
		}).collect(Collectors.toList());
		//锁定库存
		for (SkuWareHasStock skuWareHasStock : collect) {
			boolean skuLocked = false;
			Long skuId = skuWareHasStock.getSkuId();
			List<Long> wareIds = skuWareHasStock.getWareId();
			Integer count = skuWareHasStock.getCount();
			if (wareIds == null || wareIds.size() == 0) {
				throw new NoStockException(skuId.toString());
			}
			for (Long wareId : wareIds) {
				//成功返回1: 1行记录受影响
				Long successCount = baseMapper.lockSkuStock(skuId, wareId, count);
				if (successCount > 0) {
					//当前仓库锁定成功
					skuLocked = true;
					//
					WareOrderTaskDetailEntity detailEntity = new WareOrderTaskDetailEntity(null, skuId, "", count, wareOrderTaskEntity.getId(), wareId, 1);
					wareOrderTaskDetailService.save(detailEntity);
					//告诉mq我们锁了库存
					StockLockedTo stockLockedTo = new StockLockedTo();
					stockLockedTo.setTaskId(wareOrderTaskEntity.getId());
					StockDetailTo stockDetailTo = new StockDetailTo();
					BeanUtils.copyProperties(detailEntity, stockDetailTo);

					stockLockedTo.setDetailTo(stockDetailTo);
					rabbitTemplate.convertAndSend("stock-event-exchange",
							"stock.locked", stockLockedTo);

					break;
				}
			}
			if (!skuLocked) {
				throw new NoStockException(skuId.toString());
			}
		}

		//3.肯定全部锁定成功
		return true;
	}

	@Override
	public void unlockStock(StockLockedTo to) {


		//库存工作单id
		Long taskId = to.getTaskId();
		StockDetailTo detailTo = to.getDetailTo();
		Long skuId = detailTo.getSkuId();
		Long detailToId = detailTo.getId();
		//查询数据库关于这个订单的锁定库存信息是否存在
		WareOrderTaskDetailEntity byId = wareOrderTaskDetailService.getById(detailToId);
		if (byId != null) {
			//查询有没有这个订单,没有则必须解锁
			WareOrderTaskEntity orderTaskEntity = wareOrderTaskService.getById(taskId);
			String orderSn = orderTaskEntity.getOrderSn();

			//如果有这个订单,要判断订单状态,如果订单状态是已取消,则解锁,否则不能解锁
			R r = orderFeignService.getOrderStatus(orderSn);
			if (r.getCode() == 0) {
				OrderVo data = r.getData(new TypeReference<OrderVo>() {
				});

				if (data == null || data.getStatus() == 4) {
					//订单已经被取消,我们才能解锁库存
					//当前库存工作单详情,状态1,已锁定但是未解锁
					if (byId.getLockStatus() == 1) {
						unlockStock(skuId, detailTo.getWareId(), detailTo.getSkuNum(), detailToId);
					}

				}
			} else {
				throw new RuntimeException("远程服务失败");
			}
		} else {

		}

	}

	//防止订单服务卡顿,导致订单状态消息一直改不了,库存消息优先到期,查订单状态为新建状态,什么都不做就走啦
	//导致卡顿订单,永远不能解锁库存
	@Transactional
	@Override
	public void unlockStock(OrderTo orderTo) {
		String orderSn = orderTo.getOrderSn();
		//差一下最新的库存状态,防止重复解锁库存
		WareOrderTaskEntity taskEntity = wareOrderTaskService.getOrderTaskByOrderSn(orderSn);
		Long taskId = taskEntity.getId();
		//按照工作单找到所有没有解锁的库存详情进行解锁
		List<WareOrderTaskDetailEntity> entities = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
				.eq("task_id", taskId)
				.eq("lock_status", 1));
		if (entities != null && entities.size() > 0) {
			for (WareOrderTaskDetailEntity detailEntity : entities) {
				unlockStock(detailEntity.getSkuId(),
						detailEntity.getWareId(),
						detailEntity.getSkuNum(),
						detailEntity.getId());
			}

		}




	}

	private List<Long> listWareIdHasStock(Long skuId) {
		return baseMapper.listWareIdHasStock(skuId);
	}

	@Data
	class SkuWareHasStock {
		private Long skuId;
		private List<Long> wareId;
		private Integer count;
	}

}
