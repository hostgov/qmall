package com.qjx.qmall.cart.config;

import com.qjx.qmall.cart.interceptor.CartInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Ryan
 * 2021-11-20-09:57
 */
@Configuration
public class QmallWebConfig implements WebMvcConfigurer {
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new CartInterceptor()).addPathPatterns("/**");
	}
}
