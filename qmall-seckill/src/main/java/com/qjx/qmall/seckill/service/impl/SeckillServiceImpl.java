package com.qjx.qmall.seckill.service.impl;

import com.alibaba.csp.sentinel.Entry;
import com.alibaba.csp.sentinel.SphU;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.qjx.qmall.common.to.mq.SeckillOrderTo;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.common.vo.MemberEntityWithSocialVo;
import com.qjx.qmall.seckill.feign.CouponFeignService;
import com.qjx.qmall.seckill.feign.ProductFeignService;
import com.qjx.qmall.seckill.interceptor.LoginUserInterceptor;
import com.qjx.qmall.seckill.service.SeckillService;
import com.qjx.qmall.seckill.to.SecKillSkuRedisTo;
import com.qjx.qmall.seckill.vo.SeckillSessionsWithSkus;
import com.qjx.qmall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Ryan
 * 2021-12-05-20:25
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

	@Resource
	CouponFeignService couponFeignService;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	ProductFeignService productFeignService;

	@Resource
	RedissonClient redissonClient;

	@Resource
	RabbitTemplate rabbitTemplate;

	private final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";
	private final String SKUKILL_CACHE_PREFIX = "seckill:skus:";
	private final String SKU_STOCK_SEMAPHORE = "seckill:stock:"; // + 商品随机码

	@Override
	public void uploadSeckillSkuLatest3Days() {
		//1.扫描最近三天秒杀活动
		R r = couponFeignService.getLatest3DaysSession();
		if (r.getCode() == 0) {
			List<SeckillSessionsWithSkus> data = r.getData(new TypeReference<List<SeckillSessionsWithSkus>>() {
			});

			//缓存到redis
			//1.缓存活动的信息
			if(data != null) {
				saveSessionInfos(data);
				//2. 缓存活动关联的商品信息
				saveSessionSkuInfos(data);
			}

		}


	}

	public List<SecKillSkuRedisTo> blockHandler(BlockException e) {
		log.error("getCurrentSeckillSkus被先溜了");
		return null;
	}

	@SentinelResource(value = "getCurrentSeckillSkus", blockHandler = "blockHandler")
	@Override
	public List<SecKillSkuRedisTo> getCurrentSeckillSkus() {

		//1.确定当前时间属于哪个秒杀场次
		long time = new Date().getTime();
		try(Entry entry = SphU.entry("seckillSkus")) {
			Set<String> keys = stringRedisTemplate.keys(SESSIONS_CACHE_PREFIX + "*");
			for (String key : keys) {
				String replace = key.replace(SESSIONS_CACHE_PREFIX, "");
				String[] s = replace.split("_");
				Long start = Long.parseLong(s[0]);
				Long end = Long.parseLong(s[1]);
				if (time >= start && time <= end) {
					//2.获取这个秒杀场次需要的所有商品信息
					List<String> range = stringRedisTemplate.opsForList().range(key, -100, 100);
					BoundHashOperations<String, String, String> hashOps = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
					List<String> list = hashOps.multiGet(range);
					if (list != null && list.size() > 0) {
						List<SecKillSkuRedisTo> collect = list.stream().map(item -> {
							SecKillSkuRedisTo redisTo = JSON.parseObject(item, SecKillSkuRedisTo.class);
//						redisTo.setRandomCode(null);
							return redisTo;
						}).collect(Collectors.toList());
						return collect;
					}
					break;
				}
			}
		} catch (BlockException e) {
			log.error("资源被限制", e.getMessage());
		}



		return null;
	}

	@Override
	public SecKillSkuRedisTo getSkuSeckillInfo(Long skuId) {

		//找到所有需要参与秒杀的商品key信息
		BoundHashOperations<String, String, String> ops = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
		Set<String> keys = ops.keys();
		if (keys != null && keys.size() > 0) {
			String regx = "\\d_" + skuId;
			for (String key : keys) {
				if (Pattern.matches(regx, key)) {
					String s = ops.get(key);
					SecKillSkuRedisTo redisTo = JSON.parseObject(s, SecKillSkuRedisTo.class);
					//处理随机码
					Long startTime = redisTo.getStartTime();
					Long endTime = redisTo.getEndTime();
					long current = new Date().getTime();
					if (current >= startTime && current <= endTime) {

					} else {
						redisTo.setRandomCode(null);
					}
					return redisTo;
				}
			}
		}
		return null;
	}
	//TODO 上架秒杀商品的时候,每一个数据都有过期时间
	//TODO 秒杀后续的流程

	@Override
	public String kill(String killId, String key, Integer num) {
		MemberEntityWithSocialVo memberVo = LoginUserInterceptor.loginUser.get();
		//1.获取当前秒杀商品的详细信息
		BoundHashOperations<String, String, String> ops = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);

		String s = ops.get(killId);
		if (StringUtils.isEmpty(s)) {
			return null;
		} else {
			SecKillSkuRedisTo redisTo = JSON.parseObject(s, SecKillSkuRedisTo.class);
			//校验合法性
			Long startTime = redisTo.getStartTime();
			Long endTime = redisTo.getEndTime();
			long duration = endTime - startTime;
			long now = new Date().getTime();
			if (now >= startTime && now <= endTime) {
				//2.校验随机码和商品id
				String code = redisTo.getRandomCode();
				String pSessionSkuId = redisTo.getPromotionSessionId().toString() + "_" + redisTo.getSkuId().toString();
				if (code.equals(key) && killId.equals(pSessionSkuId)) {

					//3.验证购物数量是否合理
					if (num <= redisTo.getSeckillLimit().intValue()) {
						//4.验证这个人是否已经购买过,幂等性,只要秒杀成功,就去站位,userId_sessionId_skuId
						String redisKey = memberVo.getId() + "_" + pSessionSkuId;
						//自动过期

						Boolean aBoolean = stringRedisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), duration, TimeUnit.MILLISECONDS);
						if (aBoolean) {
							//占位成功说明从来没有买过
							RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + code);


							boolean b = false;
							try {
								b = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);

								if (b) {
									//秒杀成功 快速下单,发送mq消息
									String orderSn = IdWorker.getTimeId();
									SeckillOrderTo orderTo = new SeckillOrderTo();
									orderTo.setOrderSn(orderSn);

									orderTo.setMemberId(memberVo.getId());
									orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
									orderTo.setSkuId(redisTo.getSkuId());
									orderTo.setSeckillPrice(redisTo.getSeckillPrice());

									rabbitTemplate.convertAndSend(
											"order-event-exchange",
											"order.seckill.order",
											orderTo);

									return orderSn;
								}
							} catch (InterruptedException e) {
								return null;
							}




						} else {
							return null;
						}

					} else {
						return null;
					}
				} else {
					return null;
				}
			} else {
				return null;
			}
		}
		return null;
	}

	private void saveSessionInfos(List<SeckillSessionsWithSkus> data) {
		for (SeckillSessionsWithSkus session : data) {
			long startTime = session.getStartTime().getTime();
			long endTime = session.getEndTime().getTime();
			String key = SESSIONS_CACHE_PREFIX + startTime + "_" + endTime;
			if (!stringRedisTemplate.hasKey(key)) {
				List<String> collect = session.getRelationSkus().stream()
						.map(item -> item.getPromotionSessionId() + "_" + item.getSkuId().toString())
						.collect(Collectors.toList());
				stringRedisTemplate.opsForList().leftPushAll(key, collect);
			}

		}
	}

	private void saveSessionSkuInfos(List<SeckillSessionsWithSkus> data) {
		for (SeckillSessionsWithSkus session : data) {
			BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(SKUKILL_CACHE_PREFIX);
			session.getRelationSkus().forEach(seckillSkuVo -> {
				String token = UUID.randomUUID().toString().replace("-", "");
				if (!ops.hasKey(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString())) {

					//缓存商品详细信息
					SecKillSkuRedisTo redisTo = new SecKillSkuRedisTo();
					//1.sku的基本数据
					R r = productFeignService.getSkuInfo(seckillSkuVo.getSkuId());
					if (r.getCode() == 0) {
						SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
						});
						redisTo.setSkuInfo(skuInfo);
					}
					//2.sku的秒杀信息
					BeanUtils.copyProperties(seckillSkuVo, redisTo);

					//3.设置当前商品的秒杀时间信息
					redisTo.setStartTime(session.getStartTime().getTime());
					redisTo.setEndTime(session.getEndTime().getTime());

					//4.设置商品随机码

					redisTo.setRandomCode(token);

					ops.put(seckillSkuVo.getPromotionSessionId().toString() + "_" + seckillSkuVo.getSkuId().toString(), JSON.toJSONString(redisTo));

					//如果当前场次的秒杀的商品的库存信息已经上架就不需要再次上架
					//5.准备分布式信号量
					RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
					semaphore.trySetPermits(seckillSkuVo.getSeckillCount().intValue());
				}

			});
		}
	}
}
