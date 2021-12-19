package com.qjx.qmall.order.vo;

import com.qjx.qmall.order.entity.OrderEntity;
import lombok.Data;

/**
 * Ryan
 * 2021-11-23-20:36
 */
@Data
public class SubmitOrderResponseVo {
	private OrderEntity order;
	private Integer code; //0成功

}
