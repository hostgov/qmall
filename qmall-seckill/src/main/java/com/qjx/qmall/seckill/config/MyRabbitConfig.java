package com.qjx.qmall.seckill.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Ryan
 * 2021-11-21-13:36
 */
@Configuration
public class MyRabbitConfig {

//	@Resource
//	RabbitTemplate rabbitTemplate;

	@Bean
	public MessageConverter messageConverter() {
		return new Jackson2JsonMessageConverter();
	}




//	//定制RabbitTemplate
//	@PostConstruct//对象完成创建以后
//	public void initRabbitTemplate() {
//		/** Producer -> exchange success callback
//		 * Confirmation callback.
//		 * @param correlationData correlation data for the callback.
//		 * @param ack true for ack, false for nack
//		 * @param cause An optional cause, for nack, when available, otherwise null.
//		 */
//
//
//		rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
//			@Override
//			public void confirm(CorrelationData correlationData, boolean ack, String cause) {
//
//			}
//		});
//
//		/** exchange -> queue fail callback
//		 * Returned message callback.
//		 * @param message the returned message.
//		 * @param replyCode the reply code.
//		 * @param replyText the reply text.
//		 * @param exchange the exchange.
//		 * @param routingKey the routing key.
//		 */
//		rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
//			@Override
//			public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
//
//			}
//		});
//	}
}
