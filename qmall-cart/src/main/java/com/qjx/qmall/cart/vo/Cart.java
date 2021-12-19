package com.qjx.qmall.cart.vo;

import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ryan
 * 2021-11-18-21:02
 */
@ToString
public class Cart {
	List<CartItem> items;
	private Integer countNum; // 商品总数量
	private Integer countType; //商品类型数量
	private BigDecimal totalAmount; //订单总价
	private BigDecimal reduce = new BigDecimal("0.00"); //已减免价格

	public void setItems(List<CartItem> items) {
		this.items = items;
	}




	public void setReduce(BigDecimal reduce) {
		this.reduce = reduce;
	}

	public List<CartItem> getItems() {
		return items;
	}

	public Integer getCountNum() {
		int count = 0;
		if (items != null && items.size() > 0) {
			for (CartItem item : items) {
				count += item.getCount();
			}
		}
		return count;
	}

	public Integer getCountType() {
		int count = 0;
		if (items != null && items.size() > 0) {
			for (CartItem item : items) {
				count += 1;
			}
		}
		return count;
	}

	public BigDecimal getTotalAmount() {
		BigDecimal amount = new BigDecimal("0.00");
		if (items != null && items.size() > 0) {
			for (CartItem item : items) {
				if (item.getCheck()) {
					BigDecimal totalPrice = item.getTotalPrice();
					amount = amount.add(totalPrice);
				}

			}
			amount = amount.subtract(this.getReduce());
		}

		return amount;
	}

	public BigDecimal getReduce() {
		return reduce;
	}
}
