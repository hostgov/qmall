package com.qjx.qmall.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Ryan
 * 2021-11-17-15:19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("ums_member_social_relation")
public class MemberSocialRelationEntity {
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	private Long memberId;

	private String socialUid;

	private String accessToken;

	private Long expiresIn;

	private String tokenFrom;

}
