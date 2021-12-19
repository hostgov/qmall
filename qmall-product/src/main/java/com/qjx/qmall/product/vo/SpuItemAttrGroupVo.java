package com.qjx.qmall.product.vo;

import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Ryan
 * 2021-11-13-15:57
 */
@ToString
@Data
public class SpuItemAttrGroupVo {
	private String groupName;
	private List<Attr> attrs;
}
