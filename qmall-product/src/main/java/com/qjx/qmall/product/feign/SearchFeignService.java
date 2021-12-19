package com.qjx.qmall.product.feign;

import com.qjx.qmall.common.to.es.SkuEsModel;
import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * Ryan
 * 2021-10-27-21:17
 */
@FeignClient("qmall-search")
public interface SearchFeignService {

	@PostMapping("/search/save/product")
	R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList);
}
