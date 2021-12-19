package com.qjx.qmall.cart.interceptor;

import com.qjx.qmall.cart.vo.UserInfoTo;
import com.qjx.qmall.common.constant.AuthServerConstant;
import com.qjx.qmall.common.constant.CartConstant;
import com.qjx.qmall.common.constant.QmallConstant;
import com.qjx.qmall.common.vo.MemberEntityWithSocialVo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

/**
 * Ryan
 * 2021-11-20-09:39
 * 在执行目标方法之前,判断用户的登录状态,并封装传递给controller目标请求
 */
public class CartInterceptor implements HandlerInterceptor {

	public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

	//目标方法执行之前
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {



		UserInfoTo userInfoTo = new UserInfoTo();

		HttpSession session = request.getSession();
		MemberEntityWithSocialVo member = (MemberEntityWithSocialVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
		if (member != null) {
			//用户登录了
			userInfoTo.setUserId(member.getId());

		}
		Cookie[] cookies = request.getCookies();
		if (cookies != null && cookies.length > 0) {
			for (Cookie cookie : cookies) {
				String name = cookie.getName();
				if (name.equals(CartConstant.TEMP_USER_COOKIE_NAME)) {
					userInfoTo.setUserKey(cookie.getValue());
					userInfoTo.setTempUser(true);
				}
			}
		}
		if (StringUtils.isEmpty(userInfoTo.getUserKey())) {
			String uuid = UUID.randomUUID().toString();
			userInfoTo.setUserKey(uuid);
		}
		threadLocal.set(userInfoTo);
		return true;
	}

	//业务执行之后
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {


		UserInfoTo userInfoTo = threadLocal.get();

		if (userInfoTo.getUserId() == null) {
			Cookie cookie = new Cookie(CartConstant.TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
			cookie.setDomain(QmallConstant.QMALL_DOMAIN);
			cookie.setMaxAge(CartConstant.TEMP_USER_COOKIE_TIMEOUT);
			response.addCookie(cookie);
		}

	}
}
