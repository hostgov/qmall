package com.qjx.qmall.ware.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Ryan 会员收货地址列表
 * 2021-11-23-10:14
 */
@Data
public class MemberAddressVo {
	private Long id;

	@ApiModelProperty(value = "member_id")
	private Long memberId;

	@ApiModelProperty(value = "收货人姓名")
	private String name;

	@ApiModelProperty(value = "电话")
	private String phone;

	@ApiModelProperty(value = "邮政编码")
	private String postCode;

	@ApiModelProperty(value = "省份/直辖市")
	private String province;

	@ApiModelProperty(value = "城市")
	private String city;

	@ApiModelProperty(value = "区")
	private String region;

	@ApiModelProperty(value = "详细地址(街道)")
	private String detailAddress;

	@ApiModelProperty(value = "省市区代码")
	private String areacode;

	@ApiModelProperty(value = "是否默认")
	private Integer defaultStatus;
}
