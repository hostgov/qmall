package com.qjx.qmall.seckill.config;

import com.qjx.qmall.seckill.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * Ryan
 * 2021-12-06-21:17
 */
@Configuration
public class SeckillWebConfig implements WebMvcConfigurer {

	@Resource
	LoginUserInterceptor loginUserInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(loginUserInterceptor).addPathPatterns("/**");
	}
}
