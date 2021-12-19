package com.qjx.qmall.ware.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Ryan
 * 2021-11-30-16:19
 */
@FeignClient("qmall-order")
public interface OrderFeignService {
	@GetMapping("/order/order/status/{orderSn}")
	R getOrderStatus(@PathVariable("orderSn") String orderSn);
}
