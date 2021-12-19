package com.qjx.qmall.product.web;

import com.qjx.qmall.product.entity.CategoryEntity;
import com.qjx.qmall.product.service.CategoryService;
import com.qjx.qmall.product.vo.Catelog2Vo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * Ryan
 * 2021-11-02-14:02
 */
@Controller
public class IndexController {

	@Resource
	CategoryService categoryService;


	@GetMapping({"/","/index.html"})
	public String indexPage(Model model) {
		//1. 查询所有的1级分类
		List<CategoryEntity> categoryEntities = categoryService.getLevel1Categories();

		model.addAttribute("categorys", categoryEntities);
		return "index";
	}

	@ResponseBody
	@GetMapping("/index/catalog.json")
	public Map<String, List<Catelog2Vo>> getCatalogJson() {
		return categoryService.getCatalogJson();
	}
}
