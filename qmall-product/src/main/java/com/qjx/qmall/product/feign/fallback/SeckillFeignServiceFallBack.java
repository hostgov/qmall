package com.qjx.qmall.product.feign.fallback;

import com.qjx.qmall.common.exception.BizCodeEnum;
import com.qjx.qmall.common.utils.R;
import com.qjx.qmall.product.feign.SeckillFeignService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Ryan
 * 2021-12-09-20:50
 */
@Slf4j
@Component
public class SeckillFeignServiceFallBack implements SeckillFeignService {

	@Override
	public R skuSeckillInfo(Long skuId) {
		log.info("熔断方法调用");
		return R.error(BizCodeEnum.TOO_MANY_REQUEST.getCode(), BizCodeEnum.TOO_MANY_REQUEST.getMsg());
	}
}
