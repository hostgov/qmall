package com.qjx.qmall.seckill.service;

import com.qjx.qmall.seckill.to.SecKillSkuRedisTo;

import java.util.List;

/**
 * Ryan
 * 2021-12-05-20:25
 */

public interface SeckillService {
	void uploadSeckillSkuLatest3Days();

	List<SecKillSkuRedisTo> getCurrentSeckillSkus();

	SecKillSkuRedisTo getSkuSeckillInfo(Long skuId);

	String kill(String killId, String key, Integer num);
}
