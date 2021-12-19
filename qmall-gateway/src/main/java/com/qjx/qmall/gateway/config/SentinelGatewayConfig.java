package com.qjx.qmall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.qjx.qmall.common.exception.BizCodeEnum;
import com.qjx.qmall.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * Ryan
 * 2021-12-11-11:34
 */
@Configuration
public class SentinelGatewayConfig {
	public SentinelGatewayConfig () {
		GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
			@Override
			public Mono<ServerResponse> handleRequest(ServerWebExchange serverWebExchange, Throwable throwable) {
				R error = R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
				String s = JSON.toJSONString(error);
				Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(s), String.class);
				return body;
			}
		});
	}
}
