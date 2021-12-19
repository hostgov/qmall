package com.qjx.qmall.ware.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Ryan
 * 2021-10-24-11:23
 */
@FeignClient("qmall-gateway")
public interface ProductFeignService {

	@GetMapping("/api/product/skuinfo/info/{skuId}")
	R info(@PathVariable("skuId") Long skuId);
}
