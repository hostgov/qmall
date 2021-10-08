package com.qjx.qmall.member.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Ryan
 * 2021-10-08-17:00
 */
@FeignClient(value = "qmall-coupon")
public interface CouponFeignService {

	@RequestMapping("/coupon/coupon/member/list")
	R memberCoupons();

}
