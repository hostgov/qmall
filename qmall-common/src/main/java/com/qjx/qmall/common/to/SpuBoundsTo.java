package com.qjx.qmall.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Ryan
 * 2021-10-19-21:37
 */
@Data
public class SpuBoundsTo {
	private Long spuId;

	private BigDecimal buyBounds;

	private BigDecimal growBounds;
}
