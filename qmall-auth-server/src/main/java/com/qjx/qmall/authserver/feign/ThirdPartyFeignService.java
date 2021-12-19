package com.qjx.qmall.authserver.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Ryan
 * 2021-11-14-15:29
 */
@FeignClient("qmall-third-party")
public interface ThirdPartyFeignService {
	@GetMapping("/sms/sendcode")
	R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);
}
