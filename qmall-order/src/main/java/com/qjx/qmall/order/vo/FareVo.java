package com.qjx.qmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Ryan
 * 2021-11-24-15:56
 */
@Data
public class FareVo {
	private MemberAddressVo address;
	private BigDecimal fare;
}
