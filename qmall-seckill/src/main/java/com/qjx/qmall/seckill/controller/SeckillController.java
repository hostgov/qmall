package com.qjx.qmall.seckill.controller;

import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.seckill.service.SeckillService;
import com.qjx.qmall.seckill.to.SecKillSkuRedisTo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.List;

/**
 * Ryan
 * 2021-12-06-14:41
 */
@Controller
public class SeckillController {

	@Resource
	SeckillService seckillService;
	//返回当前时间可以参与的秒杀商品的信息
	@ResponseBody
	@GetMapping("/currentSeckillSkus")
	public R getCurrentSeckillSkus() {
		List<SecKillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();

		return R.ok().setData(vos);
	}

	@ResponseBody
	@GetMapping("/sku/seckill/{skuId}")
	public R skuSeckillInfo(@PathVariable("skuId") Long skuId) {
		SecKillSkuRedisTo to = seckillService.getSkuSeckillInfo(skuId);
		return R.ok().setData(to);
	}

	@GetMapping("/kill")
	public String secKill(@RequestParam("killId") String killId,
	                      @RequestParam("key") String key,
	                      @RequestParam("num") Integer num,
	                      Model model) {
		String orderSn = seckillService.kill(killId, key, num);
		model.addAttribute("orderSn", orderSn);
		//1.判断是否登录
		return "success";
	}
}
