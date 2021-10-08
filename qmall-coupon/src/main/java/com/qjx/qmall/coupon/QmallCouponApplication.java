package com.qjx.qmall.coupon;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.qjx.qmall.coupon.dao")
@ComponentScan({"com.qjx.qmall"})
@EnableDiscoveryClient
@SpringBootApplication
public class QmallCouponApplication {

	public static void main(String[] args) {
		SpringApplication.run(QmallCouponApplication.class, args);
	}

}
