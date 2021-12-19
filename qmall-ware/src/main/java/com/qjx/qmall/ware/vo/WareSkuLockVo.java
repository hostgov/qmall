package com.qjx.qmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * Ryan
 * 2021-11-24-21:08
 */
@Data
public class WareSkuLockVo {
	private String orderSn;
	private List<OrderItemVo> locks;
}
