package com.qjx.qmall.order.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * Ryan
 * 2021-11-28-20:02
 */
@Configuration
public class MyMQConfig {

	@Bean
	public Queue orderDelayQueue() {
		Map<String,Object> arguments = new HashMap<>();
		arguments.put("x-dead-letter-exchange", "order-event-exchange");
		arguments.put("x-dead-letter-routing-key", "order.release.order");
		arguments.put("x-message-ttl",60000);
		Queue queue = new Queue("order.delay.queue",
				true,
				false,
				false,
				arguments);
		return queue;

	}

	@Bean
	public Queue orderReleaseQueue() {
		Queue queue = new Queue("order.release.order.queue",
				true,
				false,
				false);
		return queue;
	}

	@Bean
	public Exchange orderEventExchange() {

		TopicExchange topicExchange = new TopicExchange("order-event-exchange", true, false);
		return topicExchange;
	}

	@Bean
	public Binding orderCreateOrderBinding () {
		Binding binding = new Binding("order.delay.queue",
				Binding.DestinationType.QUEUE,
				"order-event-exchange",
				"order.create.order",null);
		return binding;
	}

	@Bean
	public Binding orderReleaseOrderBinding () {
		Binding binding = new Binding("order.release.order.queue",
				Binding.DestinationType.QUEUE,
				"order-event-exchange",
				"order.release.order",null);
		return binding;
	}

	@Bean
	public Binding orderReleaseOtherBinding () {
		Binding binding = new Binding("stock.release.stock.queue",
				Binding.DestinationType.QUEUE,
				"order-event-exchange",
				"order.release.other.#",null);
		return binding;
	}

	@Bean
	public Queue orderSeckillOrderQueue() {
		Queue queue = new Queue("order.seckill.order.queue",
				true,
				false,
				false);
		return queue;
	}

	@Bean
	public Binding orderSeckillOrderQueueBinding () {
		Binding binding = new Binding("order.seckill.order.queue",
				Binding.DestinationType.QUEUE,
				"order-event-exchange",
				"order.seckill.order",null);
		return binding;
	}
}
