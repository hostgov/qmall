package com.qjx.qmall.order.dao;

import com.qjx.qmall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 订单
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:10:03
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

	void updateOrderStatus(@Param("outTradeNo") String outTradeNo, @Param("code") Integer code);
}
