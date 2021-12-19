package com.qjx.qmall.seckill.interceptor;

import com.qjx.qmall.common.constant.AuthServerConstant;
import com.qjx.qmall.common.vo.MemberEntityWithSocialVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Ryan
 * 2021-11-22-21:44
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

	public static ThreadLocal<MemberEntityWithSocialVo> loginUser = new ThreadLocal<>();

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		String requestURI = request.getRequestURI();
		AntPathMatcher antPathMatcher = new AntPathMatcher();
		boolean match = antPathMatcher.match("/kill", requestURI);

		if (match) {
			MemberEntityWithSocialVo attribute = (MemberEntityWithSocialVo) request.getSession().getAttribute(AuthServerConstant.LOGIN_USER);
			if (attribute != null) {
				loginUser.set(attribute);
				return true;
			} else {
				//没登录就去取登录
				request.getSession().setAttribute("msg", "请先进行登录");
				response.sendRedirect("http://auth.qmall.com/login.html");
				return false;
			}
		}

		return true;
	}
}
