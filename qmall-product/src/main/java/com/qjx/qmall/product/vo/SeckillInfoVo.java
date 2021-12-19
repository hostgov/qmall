package com.qjx.qmall.product.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Ryan
 * 2021-12-06-16:39
 */
@Data
public class SeckillInfoVo {

	private Long id;

	@ApiModelProperty(value = "活动id")
	private Long promotionId;

	@ApiModelProperty(value = "活动场次id")
	private Long promotionSessionId;

	@ApiModelProperty(value = "商品id")
	private Long skuId;

	//商品秒杀随机码
	private String randomCode;

	@ApiModelProperty(value = "秒杀价格")
	private BigDecimal seckillPrice;

	@ApiModelProperty(value = "秒杀总量")
	private BigDecimal seckillCount;

	@ApiModelProperty(value = "每人限购数量")
	private BigDecimal seckillLimit;

	@ApiModelProperty(value = "排序")
	private Integer seckillSort;

	//当前商品秒杀开始时间
	private Long startTime;
	//当前商品秒杀结束时间
	private Long endTime;
}
