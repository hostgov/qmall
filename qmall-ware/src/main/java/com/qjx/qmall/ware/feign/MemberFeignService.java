package com.qjx.qmall.ware.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Ryan
 * 2021-11-23-14:45
 */
@FeignClient("qmall-member")
public interface MemberFeignService {

	@RequestMapping("/member/memberreceiveaddress/info/{id}")
	R addrInfo(@PathVariable("id") Long id);
}
