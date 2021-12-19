package com.qjx.qmall.member.web;

import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.member.feign.OrderFeignService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Ryan
 * 2021-12-02-20:12
 */
@Controller
public class MemberWebController {

	@Resource
	OrderFeignService orderFeignService;

	@GetMapping("/memberOrder.html")
	public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model) {

		//request 验证签名,如果正确可以去修改

		Map<String, Object> page = new HashMap<>();
		page.put("page", pageNum.toString());
		R r = orderFeignService.listWithItem(page);
		model.addAttribute("orders", r);

		return "orderList";
	}
}
