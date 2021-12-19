package com.qjx.qmall.product.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Ryan
 * 2021-11-14-10:58
 */
@ConfigurationProperties(prefix = "qmall.thread")
@Component
@Data
public class ThreadPoolConfigProperties {
	private Integer coreSize;
	private Integer maxSize;
	private Integer keepAliveTime;
}
