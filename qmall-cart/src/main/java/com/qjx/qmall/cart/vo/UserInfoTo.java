package com.qjx.qmall.cart.vo;

import lombok.Data;
import lombok.ToString;

/**
 * Ryan
 * 2021-11-20-09:43
 */
@ToString
@Data
public class UserInfoTo {
	private Long userId;
	private String userKey;
	private boolean tempUser = false;
}
