package com.qjx.qmall.product.feign;

import com.qjx.qmall.common.to.SkuReductionTo;
import com.qjx.qmall.common.to.SpuBoundsTo;
import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Ryan
 * 2021-10-19-21:33
 */
@FeignClient("qmall-coupon")
public interface CouponFeignService {

	@PostMapping("/coupon/spubounds/save")
	R saveSpuBounds(@RequestBody SpuBoundsTo spuBoundsTo);

	@PostMapping("/coupon/skufullreduction/saveInfo")
	R saveSkuReduction(@RequestBody SkuReductionTo skuReductionTo);
}
