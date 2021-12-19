package com.qjx.qmall.product.feign;

import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.feign.fallback.SeckillFeignServiceFallBack;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Ryan
 * 2021-12-06-16:36
 */
@FeignClient(value = "qmall-seckill", fallback = SeckillFeignServiceFallBack.class)
public interface SeckillFeignService {

	@GetMapping("/sku/seckill/{skuId}")
	R skuSeckillInfo(@PathVariable("skuId") Long skuId);

}
