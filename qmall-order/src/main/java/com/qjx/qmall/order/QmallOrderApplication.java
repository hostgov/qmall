package com.qjx.qmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.qjx.qmall.order.dao")
@ComponentScan({"com.qjx.qmall"})
@EnableDiscoveryClient
@SpringBootApplication
public class QmallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(QmallOrderApplication.class, args);
	}

}
