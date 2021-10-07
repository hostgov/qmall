package com.qjx.qmall.order.dao;

import com.qjx.qmall.order.entity.OrderSettingEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单配置信息
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:10:03
 */
@Mapper
public interface OrderSettingDao extends BaseMapper<OrderSettingEntity> {
	
}
