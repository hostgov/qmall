package com.qjx.qmall.thirdparty.controller;

import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.thirdparty.component.SmsComponent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * Ryan
 * 2021-11-14-15:23
 */
@RestController
@RequestMapping("/sms")
public class SmsSendController {

	@Resource
	SmsComponent smsComponent;

	//提供给别的服务进行调用
	@GetMapping("/sendcode")
	public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {
		smsComponent.sendSmsCode(phone,code);
		return R.ok();
	}
}
