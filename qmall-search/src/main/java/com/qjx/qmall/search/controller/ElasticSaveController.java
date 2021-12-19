package com.qjx.qmall.search.controller;

import com.qjx.qmall.common.exception.BizCodeEnum;
import com.qjx.qmall.common.to.es.SkuEsModel;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.search.service.ProductSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * Ryan
 * 2021-10-27-20:27
 */
@Slf4j
@RequestMapping("/search/save")
@RestController
public class ElasticSaveController {

	@Resource
	ProductSaveService productSaveService;

	//上架商品
	@PostMapping("/product")
	public R productStatusUp(@RequestBody List<SkuEsModel> skuEsModelList) {

		boolean b = false;
		try {
			b = productSaveService.productStatusUp(skuEsModelList);
		} catch (IOException e) {
			log.error("ElasticSaveController商品上架错误:{}", e);
			return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
		}
		if (!b) {
			return R.ok();
		} else {
			return R.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(), BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
		}

	}
}
