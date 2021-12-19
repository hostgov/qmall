package com.qjx.qmall.order.listener;

import com.qjx.qmall.order.entity.OrderEntity;
import com.qjx.qmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Ryan
 * 2021-11-30-21:28
 */
@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

	@Resource
	OrderService orderService;

	@RabbitHandler
	public void listener(OrderEntity entity, Channel channel, Message message) throws IOException {
		try {
			orderService.closeOrder(entity);
			//手动调用支付宝收单
			
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
		}

	}

}
