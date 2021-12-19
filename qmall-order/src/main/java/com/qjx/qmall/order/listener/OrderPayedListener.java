package com.qjx.qmall.order.listener;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.qjx.qmall.order.config.AlipayTemplate;
import com.qjx.qmall.order.service.OrderService;
import com.qjx.qmall.order.vo.PayAsyncVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Ryan
 * 2021-12-04-16:41
 */
@RestController
public class OrderPayedListener {

	@Resource
	OrderService orderService;

	@Resource
	AlipayTemplate alipayTemplate;

	@PostMapping("/payed/notify")
	public String handleAliPayed(PayAsyncVo vo, HttpServletRequest request) throws AlipayApiException {
//		Map<String, String[]> map = request.getParameterMap();
//		for(String key : map.keySet()) {
//			request.getParameter(key);
//		}
		//验签
		Map<String, String> params = new HashMap<>();
		Map<String, String[]> requestParams = request.getParameterMap();
		for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
			String name = (String) iter.next();
			String[] values = (String[]) requestParams.get(name);
			String valueStr = "";
			for (int i = 0; i < values.length; i++) {
				valueStr = (i == values.length - 1)? valueStr + values[i] : valueStr + values[i] + ",";
			}
			//乱码解决,这段代码在出现乱码时使用
//			valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
			params.put(name, valueStr);
		}
		boolean signVerified = AlipaySignature.rsaCheckV1(params,
				alipayTemplate.getAlipay_public_key(),
				alipayTemplate.getCharset(),
				alipayTemplate.getSign_type());

		if (signVerified) {
			System.out.println("签名验证成功");
			String result = orderService.handlePayResult(vo);
			return result;
		} else {
			System.out.println("签名验证失败");
			return "error";
		}
	}
}
