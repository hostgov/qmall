package com.qjx.qmall.product.web;

import com.qjx.qmall.product.service.SkuInfoService;
import com.qjx.qmall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * Ryan
 * 2021-11-13-14:19
 */
@Controller
public class ItemController {

	@Resource
	SkuInfoService skuInfoService;

	@GetMapping("/{skuId}.html")
	public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
		System.out.println("准备查询" + skuId + "详情");
		SkuItemVo vo = skuInfoService.item(skuId);
		model.addAttribute("item", vo);
		System.out.println(vo.toString());
		return "item";
	}
}
