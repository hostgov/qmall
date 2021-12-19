package com.qjx.qmall.ware.listener;

import com.qjx.qmall.common.to.mq.OrderTo;
import com.qjx.qmall.common.to.mq.StockLockedTo;
import com.qjx.qmall.ware.service.WareSkuService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * Ryan
 * 2021-11-30-16:49
 */
@Service
@RabbitListener(queues = "stock.release.stock.queue")
public class StockReleaseListener {

	@Resource
	WareSkuService wareSkuService;

	//库存自动解锁
	@RabbitHandler
	public void handlerStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {

		try {
//			Boolean redelivered = message.getMessageProperties().getRedelivered();
			wareSkuService.unlockStock(to);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}


	}

	@RabbitHandler
	public void handleOrderCloseRelease(OrderTo orderTo, Message message, Channel channel) throws IOException {
		try {
			wareSkuService.unlockStock(orderTo);
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (Exception e) {
			channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
		}
	}
}
