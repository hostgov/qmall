package com.qjx.qmall.common.exception;

/**
 * Ryan
 * 2021-11-25-09:30
 */
public class NoStockException extends RuntimeException{
	private String skuId;
	public NoStockException(String skuId) {
		super("商品id:" + skuId+": 库存不足");
	}
	public String getSkuId() {
		return skuId;
	}
	public void setSkuId(String skuId) {
		this.skuId = skuId;
	}
}
