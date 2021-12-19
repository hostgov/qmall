package com.qjx.qmall.order.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Ryan
 * 2021-11-23-10:11
 */
//@Data
public class OrderConfirmVo {

	@Setter @Getter
	String orderToken; //防重令牌

	@Getter @Setter
	Map<Long, Boolean> stocks;

	public Integer getCount() {
		int i = 0;
		if (items != null) {
			for (OrderItemVo item : items) {
				i += item.getCount();
			}
		}
		return i;
	}

	//`ums_member_receive_address`收货地址
	@Setter @Getter
	List<MemberAddressVo> address;

	//所有选中的购物项
	@Setter @Getter
	List<OrderItemVo> items;

	//发票....
	//优惠券信息
	@Setter @Getter
	Integer integration;

//	BigDecimal total;//订单总额
	public BigDecimal getTotal() {
		BigDecimal sum = new BigDecimal("0");
		if (items != null) {
			for (OrderItemVo item : items) {
				sum = sum.add(item.getPrice().multiply(new BigDecimal(item.getCount().toString())));
			}
		}
		return sum;
	}

//	BigDecimal payPrice;//应付价格
	public BigDecimal getPayPrice() {
		return getTotal();
	}




}
