package com.qjx.qmall.seckill.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Ryan
 * 2021-12-05-21:45
 */
@FeignClient("qmall-product")
public interface ProductFeignService {
	@GetMapping("/product/skuinfo/info/{skuId}")
	R getSkuInfo(@PathVariable("skuId") Long skuId);
}
