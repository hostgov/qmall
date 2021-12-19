package com.qjx.qmall.authserver.controller;

import com.alibaba.fastjson.TypeReference;
import com.qjx.qmall.authserver.feign.MemberFeignService;
import com.qjx.qmall.authserver.feign.ThirdPartyFeignService;
import com.qjx.qmall.authserver.vo.UserLoginVo;
import com.qjx.qmall.authserver.vo.UserRegistVo;
import com.qjx.qmall.common.constant.AuthServerConstant;
import com.qjx.qmall.common.exception.BizCodeEnum;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.common.vo.MemberEntityWithSocialVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Ryan
 * 2021-11-14-12:12
 */
@Controller
public class LoginController {

	@Resource
	ThirdPartyFeignService thirdPartyService;

	@Resource
	StringRedisTemplate stringRedisTemplate;

	@Resource
	MemberFeignService memberFeignService;

	@ResponseBody
	@GetMapping("/sms/sendcode")
	public R sendCode(@RequestParam("phone") String phone) {
		//TODO 1 . 接口防刷

		//+61416810777
		String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone.substring(1));
		if (!StringUtils.isEmpty(redisCode)) {
			long l = Long.parseLong(redisCode.split("_")[1]);
			if (System.currentTimeMillis() - l < 60000) {
				return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(),BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
			}
		}


		String code = UUID.randomUUID().toString().substring(0, 5);
		String substring = code + "_"+ System.currentTimeMillis();


		//2. 验证码校验.redis 存key: phone  , value: code
		// sms:code:61416810777 -> code
		//sms:code: 61416810777

		stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone.substring(1), substring, 15, TimeUnit.MINUTES);

		thirdPartyService.sendCode(phone, code);
		return R.ok();
	}

//TODO 重定向携带数据，利用session原理，在数据放在session里，只要跳到下一个页面取出数据后，session中的数据就会删掉
	//TODO 分布式下的session问题
//RedirectAttributes.addFlashAttribute 在重定向时模拟转发中的Model携带数据
	@PostMapping("/regist")
	public String regist(@Valid UserRegistVo vo, BindingResult result, RedirectAttributes redirectAttributes) {
		if (result.hasErrors()) {
			Map<String, String> errors = result.getFieldErrors().stream().collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect:http://auth.qmall.com/reg.html";
		}
		//.1.校验验证码
		String code = vo.getCode();
		String key = AuthServerConstant.SMS_CODE_CACHE_PREFIX  + "61" + (vo.getPhone().substring(1));
		String s = stringRedisTemplate.opsForValue().get(key);
		if (!StringUtils.isEmpty(s) && code.equals(s.split("_")[0])) {
				//删除验证码
			stringRedisTemplate.delete(key);
				//验证码通过,真正注册,调用远程服务进行注册
			R r = memberFeignService.memberRegister(vo);
			if (r.getCode() == 0) {
				////注册成功回到首页,回到登录页
				return "redirect:http://auth.qmall.com/login.html";
			} else {
				//失败
				Map<String, String> errors = new HashMap<>();
				errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
				redirectAttributes.addFlashAttribute("errors", errors);
				return "redirect:http://auth.qmall.com/reg.html";
			}

		} else {
			Map<String, String> errors = new HashMap<>();
			redirectAttributes.addFlashAttribute("errors", errors.put("code","验证码错误"));
			return "redirect:http://auth.qmall.com/reg.html";
		}
	}

	@GetMapping("/login.html")
	public String loginPage(HttpSession session) {
		Object attribute = session.getAttribute(AuthServerConstant.LOGIN_USER);
		if (attribute == null) {
			return "login";
		} else {
			return "redirect:http://qmall.com";
		}

	}



	@PostMapping("/login")
	public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession session) {
		//远程登录
		R r = memberFeignService.login(vo);
		if (r.getCode() == 0) {
			//成功
			MemberEntityWithSocialVo data = r.getData(new TypeReference<MemberEntityWithSocialVo>() {
			});
			session.setAttribute(AuthServerConstant.LOGIN_USER, data);
			return "redirect:http://qmall.com";

		} else {
			Map<String, String> errors = new HashMap<>();
			errors.put("msg", r.getData(new TypeReference<String>(){}));
			redirectAttributes.addFlashAttribute("errors", errors);
			return "redirect:http://auth.qmall.com/login.html";
		}


	}

}
