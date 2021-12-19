package com.qjx.qmall.order.feign;

import com.qjx.qmall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * Ryan
 * 2021-11-23-11:17
 */
@FeignClient("qmall-cart")
public interface CartFeignService {

	@GetMapping("/currentUserCartItems")
	List<OrderItemVo> getCurrentUserCartItems();
}
