package com.qjx.qmall.authserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableFeignClients
@EnableRedisHttpSession //整合redis作为session存储
@EnableDiscoveryClient
@SpringBootApplication
public class QmallAuthServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(QmallAuthServerApplication.class, args);
	}

}
