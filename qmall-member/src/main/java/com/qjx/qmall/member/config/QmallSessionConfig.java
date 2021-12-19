package com.qjx.qmall.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Ryan
 * 2021-11-18-10:41
 */
@Configuration
public class QmallSessionConfig {
	@Bean
	public CookieSerializer cookieSerializer () {
		DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
		cookieSerializer.setDomainName("qmall.com");
		cookieSerializer.setCookieName("QMALLSESSION");

		return cookieSerializer;
	}

	@Bean
	public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
		return new GenericJackson2JsonRedisSerializer();
	}
}
