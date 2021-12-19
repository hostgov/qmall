package com.qjx.qmall.common.to.mq;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Ryan
 * 2021-12-07-21:27
 */
@Data
public class SeckillOrderTo {
	private String orderSn;
	private Long promotionSessionId;
	private Long skuId;
	private BigDecimal seckillPrice;
	private Integer num;

	private Long memberId;

}
