package com.qjx.qmall.order.dao;

import com.qjx.qmall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:10:03
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {
	
}
