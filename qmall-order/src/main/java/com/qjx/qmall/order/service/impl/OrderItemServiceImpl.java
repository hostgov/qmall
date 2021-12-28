package com.qjx.qmall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qjx.qmall.common.utils.PageUtils;
import com.qjx.qmall.common.utils.Query;
import com.qjx.qmall.order.dao.OrderItemDao;
import com.qjx.qmall.order.entity.OrderItemEntity;
import com.qjx.qmall.order.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
                new Query<OrderItemEntity>().getPage(params),
                new QueryWrapper<>()
        );

        return new PageUtils(page);
    }

//    @RabbitListener(queues = "Hello-java-queue")
//    public void recieveMessage(Message message,
//                               OrderReturnReasonEntity content,
//                               Channel channel) {
//        message.getBody();
//        message.getMessageProperties();
//        System.out.println("接收到消息:" + message + "内容为:" + content + "通道:" + channel);
//    }

}
