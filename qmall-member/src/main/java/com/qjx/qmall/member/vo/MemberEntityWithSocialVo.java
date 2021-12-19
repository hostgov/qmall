package com.qjx.qmall.member.vo;

import com.qjx.qmall.member.entity.MemberEntity;
import lombok.Data;

/**
 * Ryan
 * 2021-11-17-21:04
 */
@Data
public class MemberEntityWithSocialVo extends MemberEntity {

	private String socialUid;

	private String accessToken;

	private Long expiresIn;

	private String tokenFrom;
}
