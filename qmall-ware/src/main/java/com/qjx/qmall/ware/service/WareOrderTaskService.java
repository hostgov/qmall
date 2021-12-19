package com.qjx.qmall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.ware.entity.WareOrderTaskEntity;

import java.util.Map;

/**
 * 库存工作单
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:11:12
 */
public interface WareOrderTaskService extends IService<WareOrderTaskEntity> {

    PageUtils queryPage(Map<String, Object> params);

	WareOrderTaskEntity getOrderTaskByOrderSn(String orderSn);
}

