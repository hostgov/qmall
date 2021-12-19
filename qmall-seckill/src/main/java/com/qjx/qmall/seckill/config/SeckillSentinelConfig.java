package com.qjx.qmall.seckill.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.csp.sentinel.slots.block.authority.AuthorityException;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeException;
import com.alibaba.csp.sentinel.slots.block.flow.FlowException;
import com.alibaba.csp.sentinel.slots.block.flow.param.ParamFlowException;
import com.alibaba.csp.sentinel.slots.system.SystemBlockException;
import com.alibaba.fastjson.JSON;
import com.qjx.qmall.common.utils.R;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Ryan
 * 2021-12-09-16:00
 */
@Configuration
public class SeckillSentinelConfig implements BlockExceptionHandler {


	@Override
	public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
		//BlockException 异常接口,其子类为Sentinel五中规则异常的实现类

		//1. AuthorityException 授权异常
		//2. DegradeException 降级异常
		//3. FlowException 限流异常
		//4. ParamFlowException 参数限流异常
		//5. SystemBlockException 系统负载异常
		String msg = null;
		if (e instanceof FlowException) {
			msg = "被限流了";
		} else if (e instanceof DegradeException) {
			msg = "被降级了";
		} else if (e instanceof ParamFlowException) {
			msg = "热点参数限流";
		} else if (e instanceof SystemBlockException) {
			msg = "系统规则(负载/..不满足要全球)";
		} else if (e instanceof AuthorityException) {
			msg = "授权规则不通过";
		}
		R error = R.error(500, msg);
		httpServletResponse.setContentType("application/json");
		httpServletResponse.setCharacterEncoding("UTF-8");
		httpServletResponse.getWriter().write(JSON.toJSONString(error));
	}
}
