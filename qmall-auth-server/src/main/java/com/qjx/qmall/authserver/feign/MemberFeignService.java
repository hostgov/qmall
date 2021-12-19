package com.qjx.qmall.authserver.feign;

import com.qjx.qmall.authserver.vo.UserLoginVo;
import com.qjx.qmall.authserver.vo.UserRegistVo;
import com.qjx.qmall.authserver.vo.WeiboTokenResponse;
import com.qjx.qmall.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Ryan
 * 2021-11-16-11:06
 */
@FeignClient("qmall-member")
public interface MemberFeignService {
	@PostMapping("/member/member/regist")
	R memberRegister(@RequestBody UserRegistVo vo);

	@PostMapping("/member/member/login")
	R login(@RequestBody UserLoginVo vo);

	@PostMapping("/member/member/weibo/oauth2/login")
	R WeiboOauthLogin(@RequestBody WeiboTokenResponse weiboTokenResponse) throws Exception;
}
