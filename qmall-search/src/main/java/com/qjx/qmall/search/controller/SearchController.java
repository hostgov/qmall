package com.qjx.qmall.search.controller;

import com.qjx.qmall.search.service.MallSearchService;
import com.qjx.qmall.search.vo.SearchParam;
import com.qjx.qmall.search.vo.SearchResult;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Ryan
 * 2021-11-06-15:31
 */
@Controller
public class SearchController {

	@Resource
	MallSearchService mallSearchService;

	@GetMapping("/list.html")
	public String listPage(SearchParam param, Model model, HttpServletRequest request){
		param.set_queryString(request.getQueryString());
		SearchResult result = mallSearchService.search(param);
		model.addAttribute("result", result);
		return "list";
	}
}
