package com.qjx.qmall.order.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Ryan
 * 2021-11-23-11:12
 */
@FeignClient("qmall-product")
public interface ProductFeignService {

	@GetMapping("/product/spuinfo/skuId/{id}")
	R getSpuInfoBySkuId(@PathVariable("id") Long skuId);


}
