package com.qjx.qmall.order;

import com.qjx.qmall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Date;

@Slf4j
@SpringBootTest
class QmallOrderApplicationTests {


	@Resource
	AmqpAdmin amqpAdmin;

	@Resource
	RabbitTemplate rabbitTemplate;

	@Test
	void createExchange() {
		DirectExchange directExchange = new DirectExchange(
				"hello-java-exchange",
				true,
				false
		);
		amqpAdmin.declareExchange(directExchange);
		log.info("exchange[{}]创建成功", directExchange.getName());
	}

	@Test
	void createQueue() {
		Queue queue = new Queue(
				"Hello-java-queue",
				true,
				false,
				false);
		amqpAdmin.declareQueue(queue);
		log.info("Queue[{}]创建成功",queue.getName());
	}

	@Test
	void createBinding() {
		Binding binding = new Binding(
				"Hello-java-queue",
				Binding.DestinationType.QUEUE,
				"hello-java-exchange",
				"hello.java",
				null);
		amqpAdmin.declareBinding(binding);
		log.info("Queue[{}]创建成功","hello-java-binding");
	}


	@Test
	void sendMessage() {
		OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
		orderReturnReasonEntity.setId(1L);
		orderReturnReasonEntity.setCreateTime(new Date());
		orderReturnReasonEntity.setName("nihao");
//		String s = "hello, world!";
		rabbitTemplate.convertAndSend("hello-java-exchange","hello.java",orderReturnReasonEntity);
		log.info("消息发送完成");
	}


	@Test
	void getMessage() {
	}

	@Test
	void contextLoads() {
	}

}
