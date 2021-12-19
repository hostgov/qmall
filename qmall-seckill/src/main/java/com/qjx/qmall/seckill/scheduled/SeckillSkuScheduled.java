package com.qjx.qmall.seckill.scheduled;

import com.qjx.qmall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * Ryan 秒杀商品定时上架
 * 2021-12-05-20:05
 * 每天晚上3点,上架最近三天需要秒杀的商品
 * 当天00:00:00 - 23:59:59
 * 明天00:00:00 - 23:59:59
 * 后天
 */
@Slf4j
@Service
public class SeckillSkuScheduled {

	@Resource
	SeckillService seckillService;

	@Resource
	RedissonClient redissonClient;

	private final String upload_lock = "seckill:upload:lock";


//	@Scheduled(cron = "0 0 3 * * ?")
	@Scheduled(cron = "0 * * * * ?")
	public void uploadSeckillSkuLatest3Days() {
		//1.重复上架无需处理
		log.info("上架秒杀的商品");
		//为了保证上架秒杀商品的幂等性,加分布式锁,获取到锁的人执行
		RLock lock = redissonClient.getLock(upload_lock);
		lock.lock(10, TimeUnit.SECONDS);


		try {
			seckillService.uploadSeckillSkuLatest3Days();
		} finally {
			lock.unlock();
		}

	}
}
