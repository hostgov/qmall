package com.qjx.qmall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.common.utils.PageUtils;
import com.qjx.qmall.coupon.entity.SeckillSessionEntity;

import java.util.Map;

/**
 * 秒杀活动场次
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:02:51
 */
public interface SeckillSessionService extends IService<SeckillSessionEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

