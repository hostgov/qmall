package com.qjx.qmall.authserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.qjx.qmall.authserver.feign.MemberFeignService;
import com.qjx.qmall.common.vo.MemberEntityWithSocialVo;
import com.qjx.qmall.authserver.vo.WeiboTokenResponse;
import com.qjx.qmall.common.utils.HttpUtils;
import com.qjx.qmall.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**处理社交登录请求
 * Ryan
 * 2021-11-17-14:08
 */
@Slf4j
@Controller
public class OAuth2Controller {

	@Resource
	MemberFeignService memberFeignService;


	@GetMapping("/oauth2.0/weibo/success")
	public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
		//根据code换取accessToken
		Map<String, String> query = new HashMap<>();
		query.put("client_id", "1357988921");
		query.put("client_secret", "7d21c29fdbaa6f66ae42b21d0178fa11");
		query.put("grant_type", "authorization_code");
		query.put("redirect_uri", "http://auth.qmall.com/oauth2.0/weibo/success");
		query.put("code", code);


		HttpResponse response = HttpUtils.doPost("https://api.weibo.com",
				"/oauth2/access_token",
				"POST",
				new HashMap<>(),
				query,
				"");
		if (response.getStatusLine().getStatusCode() == 200) {
			//成功
			String entityJson = EntityUtils.toString(response.getEntity());
			WeiboTokenResponse weiboTokenResponse = JSON.parseObject(entityJson, WeiboTokenResponse.class);

			//远程登录
			R r = memberFeignService.WeiboOauthLogin(weiboTokenResponse);
			if (r.getCode() == 0) {

				MemberEntityWithSocialVo data = r.getData(new TypeReference<MemberEntityWithSocialVo>() {
				});
				log.info("用户信息:" + data.toString());
				//2. 登录成功就跳回首页
				// a. session作用域放大
				// b. 解决session序列化机制
				session.setAttribute("loginUser",data);
				return "redirect:http://qmall.com";
			} else {
				return "redirect:http://auth.qmall.com/login.html";
			}
		} else {
			return "redirect:http://auth.qmall.com/login.html";
		}


	}
}
