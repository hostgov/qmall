package com.qjx.qmall.cart.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Ryan
 * 2021-11-20-11:39
 */
@FeignClient("qmall-product")
public interface ProductFeignService {

	@GetMapping("/product/skuinfo/info/{skuId}")
	R info(@PathVariable("skuId") Long skuId);

	@GetMapping("/product/skusaleattrvalue/stringList/{skuId}")
	List<String> getSkuSaleAttrValues(@PathVariable("skuId") Long skuId);

	@GetMapping("/product/skuinfo/{skuId}/price")
	R getPrice(@PathVariable("skuId") Long skuId);
}
