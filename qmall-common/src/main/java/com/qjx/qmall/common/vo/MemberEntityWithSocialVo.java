package com.qjx.qmall.common.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;

/**
 * Ryan
 * 2021-11-17-21:04
 */
@EqualsAndHashCode(callSuper = true)
@ToString
@Data
public class MemberEntityWithSocialVo extends MemberEntity implements Serializable {

	private String socialUid;

	private String accessToken;

	private Long expiresIn;

	private String tokenFrom;
}
