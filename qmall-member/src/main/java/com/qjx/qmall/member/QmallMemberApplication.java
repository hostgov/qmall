package com.qjx.qmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableRedisHttpSession
@MapperScan("com.qjx.qmall.member.dao")
@ComponentScan({"com.qjx.qmall"})
@EnableFeignClients(basePackages = "com.qjx.qmall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class QmallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(QmallMemberApplication.class, args);
	}

}
