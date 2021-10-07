package com.qjx.qmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.common.utils.PageUtils;
import com.qjx.qmall.coupon.entity.MemberPriceEntity;

import java.util.Map;

/**
 * 商品会员价格
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:02:51
 */
public interface MemberPriceService extends IService<MemberPriceEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

