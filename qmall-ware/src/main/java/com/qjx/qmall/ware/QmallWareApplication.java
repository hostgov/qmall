package com.qjx.qmall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.qjx.qmall.ware.dao")
@ComponentScan({"com.qjx.qmall"})
@SpringBootApplication
public class QmallWareApplication {

	public static void main(String[] args) {
		SpringApplication.run(QmallWareApplication.class, args);
	}

}
