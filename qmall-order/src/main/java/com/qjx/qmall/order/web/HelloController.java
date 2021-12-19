package com.qjx.qmall.order.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Ryan
 * 2021-11-21-20:01
 */
@Controller
public class HelloController {

	@GetMapping("/{page}.html")
	public String listPage(@PathVariable("page") String page) {
		return page;
	}
}
