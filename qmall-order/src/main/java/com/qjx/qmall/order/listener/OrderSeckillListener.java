package com.qjx.qmall.order.listener;

import com.qjx.qmall.common.to.mq.SeckillOrderTo;
import com.qjx.qmall.order.entity.OrderEntity;
import com.qjx.qmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Ryan
 * 2021-12-07-21:45
 */
@Slf4j
@RabbitListener(queues = "order.seckill.order.queue")
@Component
public class OrderSeckillListener {

	@Resource
	OrderService orderService;

	@RabbitHandler
	public void listener(SeckillOrderTo seckillOrderTo, Channel channel, Message message) throws IOException {
		try {
			log.info("准备创建秒杀单的详细信息");
			orderService.createSeckillOrder(seckillOrderTo);
			//手动调用支付宝收单

			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}

	}
}
