package com.qjx.qmall.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableRabbit
@ComponentScan({"com.qjx.qmall"})
@EnableDiscoveryClient
@SpringBootApplication
@EnableFeignClients("com.qjx.qmall.ware.feign")
public class QmallWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(QmallWareApplication.class, args);
	}

}
