package com.qjx.qmall.order.vo;

import lombok.Data;

/**
 * Ryan
 * 2021-11-24-21:12
 */
@Data
public class LockStockResult {

	private Long skuId;
	private Integer num;
	private boolean locked;
}
