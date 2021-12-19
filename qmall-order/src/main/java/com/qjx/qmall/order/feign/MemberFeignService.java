package com.qjx.qmall.order.feign;

import com.qjx.qmall.order.vo.MemberAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * Ryan
 * 2021-11-23-10:37
 */

@FeignClient("qmall-member")
public interface MemberFeignService {


	@GetMapping("/member/memberreceiveaddress/{memberId}/address")
	List<MemberAddressVo> getAddress(@PathVariable("memberId") Long memberId);

}


