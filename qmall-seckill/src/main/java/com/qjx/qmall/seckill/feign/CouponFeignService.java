package com.qjx.qmall.seckill.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Ryan
 * 2021-12-05-20:35
 */
@FeignClient("qmall-coupon")
public interface CouponFeignService {
	@GetMapping("/coupon/seckillsession/latest3DaysSession")
	R getLatest3DaysSession();
}
