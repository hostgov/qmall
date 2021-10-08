package com.qjx.qmall.common.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Ryan
 * 2021-09-30-20:40
 */

@Configuration
@EnableSwagger2
public class Swagger2Config {

	@Bean
	public Docket productApiConfig(){
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("productApi")
				.apiInfo(productApiInfo())
				.select()
				.paths(Predicates.and(PathSelectors.regex("/product/.*")))
				.build();
	}

	private ApiInfo productApiInfo() {
		return new ApiInfoBuilder()
				.title("qmall商品服务API文档")
				.description("本文档描述了qmall商品服务各个模块的接口的调用方式")
				.version("1.0")
				.contact(new Contact("Hostgov", "http://qjx.com", "zmryanq@gmail.com"))
				.build();

	}


	@Bean
	public Docket memberApiConfig(){
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("memberApi")
				.apiInfo(memberApiInfo())
				.select()
				.paths(Predicates.and(PathSelectors.regex("/member/.*")))
				.build();
	}

	private ApiInfo memberApiInfo() {
		return new ApiInfoBuilder()
				.title("qmall会员服务API文档")
				.description("本文档描述了qmall会员服务各个模块的接口的调用方式")
				.version("1.0")
				.contact(new Contact("Hostgov", "http://qjx.com", "zmryanq@gmail.com"))
				.build();

	}

	@Bean
	public Docket orderApiConfig(){
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("orderApi")
				.apiInfo(orderApiInfo())
				.select()
				.paths(Predicates.and(PathSelectors.regex("/order/.*")))
				.build();
	}

	private ApiInfo orderApiInfo() {
		return new ApiInfoBuilder()
				.title("qmall订单服务API文档")
				.description("本文档描述了qmall订单服务各个模块的接口的调用方式")
				.version("1.0")
				.contact(new Contact("Hostgov", "http://qjx.com", "zmryanq@gmail.com"))
				.build();

	}

	@Bean
	public Docket couponApiConfig(){
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("couponApi")
				.apiInfo(couponApiInfo())
				.select()
				.paths(Predicates.and(PathSelectors.regex("/coupon/.*")))
				.build();
	}

	private ApiInfo couponApiInfo() {
		return new ApiInfoBuilder()
				.title("qmall优惠券服务API文档")
				.description("本文档描述了qmall优惠券服务各个模块的接口的调用方式")
				.version("1.0")
				.contact(new Contact("Hostgov", "http://qjx.com", "zmryanq@gmail.com"))
				.build();

	}

	@Bean
	public Docket wareApiConfig(){
		return new Docket(DocumentationType.SWAGGER_2)
				.groupName("wareApi")
				.apiInfo(wareApiInfo())
				.select()
				.paths(Predicates.and(PathSelectors.regex("/ware/.*")))
				.build();
	}

	private ApiInfo wareApiInfo() {
		return new ApiInfoBuilder()
				.title("qmall仓库服务API文档")
				.description("本文档描述了qmall仓库服务各个模块的接口的调用方式")
				.version("1.0")
				.contact(new Contact("Hostgov", "http://qjx.com", "zmryanq@gmail.com"))
				.build();

	}

}
