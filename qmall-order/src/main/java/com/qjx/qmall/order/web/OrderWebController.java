package com.qjx.qmall.order.web;

import com.qjx.qmall.common.exception.NoStockException;
import com.qjx.qmall.order.service.OrderService;
import com.qjx.qmall.order.vo.OrderConfirmVo;
import com.qjx.qmall.order.vo.OrderSubmitVo;
import com.qjx.qmall.order.vo.SubmitOrderResponseVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

/**
 * Ryan
 * 2021-11-22-21:41
 */
@Controller
public class OrderWebController {

	@Resource
	OrderService orderService;

	@GetMapping("/toTrade")
	public String toTrade(Model model) throws ExecutionException, InterruptedException {
		OrderConfirmVo confirmVo = orderService.confirmOrder();
		model.addAttribute("orderConfirmData", confirmVo);
		return "confirm";

	}

	//下单功能
	@PostMapping("/submitOrder")
	public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {
		try {
			SubmitOrderResponseVo responseVo = orderService.submitOrder(vo);


			//下单失败回到订单确认页重新确认订单信息

			if (responseVo.getCode() == 0) {
				//下单成功来到支付选择页
				model.addAttribute("submitOrderResp", responseVo);
				return "pay";
			} else {
				String msg = "下单失败:";
				switch (responseVo.getCode()) {
					case 1: msg += "订单信息过期,请刷新再提交"; break;
					case 2: msg += "订单商品价格发生变化,请确认后再次提交"; break;
					case 3: msg += "库存锁定失败,商品库存不足";break;
				}
				redirectAttributes.addFlashAttribute("msg",msg);
				return "redirect:http://order.qmall.com/toTrade";
			}
		} catch (Exception e) {
			if (e instanceof NoStockException) {
				String message = ((NoStockException) e).getMessage();
				redirectAttributes.addFlashAttribute("msg", message);
			}
			return "redirect:http://order.qmall.com/toTrade";
		}

	}
}
