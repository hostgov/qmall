package com.qjx.qmall.seckill.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Ryan
 * 2021-12-05-21:41
 */
@Data
public class SkuInfoVo {
	private Long skuId;

	@ApiModelProperty(value = "spuId")
	private Long spuId;

	@ApiModelProperty(value = "sku名称")
	private String skuName;

	@ApiModelProperty(value = "sku介绍描述")
	private String skuDesc;

	@ApiModelProperty(value = "所属分类id")
	private Long catalogId;

	@ApiModelProperty(value = "品牌id")
	private Long brandId;

	@ApiModelProperty(value = "默认图片")
	private String skuDefaultImg;

	@ApiModelProperty(value = "标题")
	private String skuTitle;

	@ApiModelProperty(value = "副标题")
	private String skuSubtitle;

	@ApiModelProperty(value = "价格")
	private BigDecimal price;

	@ApiModelProperty(value = "销量")
	private Long saleCount;
}
