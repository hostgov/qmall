package com.qjx.qmall.authserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Ryan 视图映射
 * 2021-11-14-13:37
 */
@Configuration
public class QmallWebConfig implements WebMvcConfigurer {
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/reg.html").setViewName("reg");
	}
}
