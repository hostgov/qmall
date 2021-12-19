package com.qjx.qmall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Ryan
 * 2021-11-23-12:15
 */
@Configuration
public class QmallFeignConfig {

	@Bean
	public RequestInterceptor requestInterceptor() {
		return new RequestInterceptor(){

			@Override
			public void apply(RequestTemplate requestTemplate) {
				//spring 利用ThreadLocal 保存了request的信息
				ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
				if (requestAttributes!= null) {
					HttpServletRequest request = requestAttributes.getRequest();//老请求
					if (request != null) {
						//同步请求头数据 Cookie
						String cookie = request.getHeader("Cookie");
						requestTemplate.header("Cookie", cookie);
					}
				}


			}
		};
	}
}
