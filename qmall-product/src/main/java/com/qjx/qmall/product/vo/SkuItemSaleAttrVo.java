package com.qjx.qmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Ryan
 * 2021-11-13-15:59
 */
@Data
@ToString
public class SkuItemSaleAttrVo {
	private Long attrId;

	private String attrName;

	private List<AttrValueWithSkuIdVo> attrValues;
}
