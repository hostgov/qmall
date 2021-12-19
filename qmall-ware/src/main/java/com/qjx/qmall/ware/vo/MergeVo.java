package com.qjx.qmall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * Ryan
 * 2021-10-23-21:59
 */
@Data
public class MergeVo {

	private  Long purchaseId;
	private List<Long> items;
}
