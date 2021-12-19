package com.qjx.qmall.product.feign;

import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Ryan
 * 2021-10-26-22:13
 */
@FeignClient("qmall-ware")
public interface WareFeignService {

	@PostMapping("/ware/waresku/hasstock")
	R getSkusHasStock(@RequestBody List<Long> skuIds);
}
