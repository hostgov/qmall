package com.qjx.qmall.order.web;

import com.alipay.api.AlipayApiException;
import com.qjx.qmall.order.config.AlipayTemplate;
import com.qjx.qmall.order.service.OrderService;
import com.qjx.qmall.order.vo.PayVo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Ryan
 * 2021-12-02-19:43
 */
@Controller
public class PayWebController {

	@Resource
	AlipayTemplate alipayTemplate;


	@Resource
	OrderService orderService;


	@ResponseBody
	@GetMapping(value = "/payOrder", produces = "text/html")
	public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {


		PayVo payVo = orderService.getOrderPay(orderSn);

//		PayVo payVo = new PayVo();
//		payVo.setOut_trade_no(orderSn);
//		payVo.setBody();//订单备注
//		payVo.setSubject();//订单主题
//		payVo.setTotal_amount();//订单金额
		String pay = alipayTemplate.pay(payVo);
		System.out.println(pay);
		return "hello";
	}
}
