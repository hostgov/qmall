package com.qjx.qmall.coupon.dao;

import com.qjx.qmall.coupon.entity.CouponEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 优惠券信息
 * 
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:02:51
 */
@Mapper
public interface CouponDao extends BaseMapper<CouponEntity> {
	
}
