package com.qjx.qmall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.qjx.qmall.common.to.mq.SeckillOrderTo;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.order.entity.OrderEntity;
import com.qjx.qmall.order.vo.*;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * 订单
 *
 * @author hostgov
 * @email zmryanq@gmail.com
 * @date 2021-10-07 20:10:03
 */
public interface OrderService extends IService<OrderEntity> {

    PageUtils queryPage(Map<String, Object> params);

	OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

	SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);

	OrderEntity getOrderByOrderSn(String orderSn);

	void closeOrder(OrderEntity entity);

	PayVo getOrderPay(String orderSn);

	PageUtils queryPageWithItem(Map<String, Object> params);

	String handlePayResult(PayAsyncVo vo);

	void createSeckillOrder(SeckillOrderTo seckillOrderTo);
}

