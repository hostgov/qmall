package com.qjx.qmall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Ryan
 * 2021-11-23-15:25
 */
@Data
public class FareVo {
	private MemberAddressVo address;

	private BigDecimal fare;
}
