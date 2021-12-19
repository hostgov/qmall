package com.qjx.qmall.order.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Ryan
 * 2021-11-24-20:11
 */
@Data
public class SpuInfoVo {

	private Long id;

	@ApiModelProperty(value = "商品名称")
	private String spuName;

	@ApiModelProperty(value = "商品描述")
	private String spuDescription;

	@ApiModelProperty(value = "所属分类id")
	private Long catalogId;

	@ApiModelProperty(value = "品牌id")
	private Long brandId;

	private BigDecimal weight;

	@ApiModelProperty(value = "上架状态[0 - 下架，1 - 上架]")
	private Integer publishStatus;

	private Date createTime;

	private Date updateTime;
}
