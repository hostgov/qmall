package com.qjx.qmall.member.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * Ryan
 * 2021-12-02-21:08
 */
@FeignClient("qmall-order")
public interface OrderFeignService {

	@PostMapping("/order/order/listWithItem")
	R listWithItem(@RequestBody Map<String, Object> params);

}
