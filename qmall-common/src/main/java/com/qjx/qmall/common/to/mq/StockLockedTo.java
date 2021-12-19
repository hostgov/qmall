package com.qjx.qmall.common.to.mq;

import lombok.Data;

/**
 * Ryan
 * 2021-11-30-15:26
 */
@Data
public class StockLockedTo {
	private Long taskId;
	private StockDetailTo detailTo;
}
