package com.qjx.qmall.ware.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Ryan
 * 2021-10-24-10:50
 */
@Data
public class PurchaseDoneVo {

	@NotNull
	private Long id;

	private List<PurchaseItemDoneVo> items;
}
