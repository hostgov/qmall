package com.qjx.qmall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ryan
 * 2021-11-05-22:04
 */
@Configuration
public class MyRedissonConfig {

	@Value("${spring.redis.host}")
	private String redisHost;

	@Bean(destroyMethod = "shutdown")
	RedissonClient redisson() {
		Config config = new Config();
//		config.useSingleServer().setAddress("redis://192.168.56.10:6379");
		config.useSingleServer().setAddress("redis://"+ redisHost + ":6379");
		return Redisson.create(config);
	}
}
