package com.qjx.qmall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Ryan
 * 2021-11-23-19:57
 */

@Data
public class OrderSubmitVo {
	private Long addrId; //收货地址id
	private Integer payType;//支付方式
	//无需提交需要购买的商品, 去购物车再获取一遍
	//优惠,发票,物流信息
	private String orderToken;//防重令牌
	private BigDecimal payPrice;//应付价格 验价
	//用户相关信息都在session中
	private String note; //订单备注
}
