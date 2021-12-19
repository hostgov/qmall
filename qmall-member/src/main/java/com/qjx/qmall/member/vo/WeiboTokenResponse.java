package com.qjx.qmall.member.vo;


import lombok.Data;

@Data
public class WeiboTokenResponse {

	private String accessToken;
	private String remindIn;
	private long expiresIn;
	private String uid;
	private String isRealName;

	private String tokenFrom = "weibo";

}
