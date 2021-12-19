package com.qjx.qmall.member.config;

import com.qjx.qmall.member.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Ryan
 * 2021-12-02-20:14
 */
@Configuration
public class MemberWebConfig implements WebMvcConfigurer {

	@Resource
	LoginUserInterceptor loginUserInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
	}
}
