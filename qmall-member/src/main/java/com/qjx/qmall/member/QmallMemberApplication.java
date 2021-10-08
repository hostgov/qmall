package com.qjx.qmall.member;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@MapperScan("com.qjx.qmall.member.dao")
@ComponentScan({"com.qjx.qmall"})
@SpringBootApplication
public class QmallMemberApplication {

	public static void main(String[] args) {
		SpringApplication.run(QmallMemberApplication.class, args);
	}

}
