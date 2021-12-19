package com.qjx.qmall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Ryan
 * 2021-11-14-10:53
 */
//@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class MyThreadConfig {
	@Bean
	public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties) {
		return new ThreadPoolExecutor(
				threadPoolConfigProperties.getCoreSize(),
				threadPoolConfigProperties.getMaxSize(),
				threadPoolConfigProperties.getKeepAliveTime(),
				TimeUnit.SECONDS,
				new LinkedBlockingQueue<>(100000),
				Executors.defaultThreadFactory(),
				new ThreadPoolExecutor.AbortPolicy());
	}
}
