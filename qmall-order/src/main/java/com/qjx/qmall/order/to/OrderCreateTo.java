package com.qjx.qmall.order.to;

import com.qjx.qmall.order.entity.OrderEntity;
import com.qjx.qmall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Ryan
 * 2021-11-24-15:43
 */
@Data
public class OrderCreateTo {
	private OrderEntity order;

	private List<OrderItemEntity> orderItems;

	private BigDecimal payPrice;

	private BigDecimal fare;//运费
}
