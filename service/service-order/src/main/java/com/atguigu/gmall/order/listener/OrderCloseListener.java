package com.atguigu.gmall.order.listener;


import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.to.mq.OrderCreateMsg;
import com.atguigu.gmall.order.service.OrderBizService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Slf4j
@Service
public class OrderCloseListener {
    @Autowired
    OrderBizService orderBizService;

    @RabbitListener(queues = MqConst.QUEUE_ORDER_DEAD)
    public void closeListener(Message message, Channel channel) throws IOException {
        MessageProperties properties = message.getMessageProperties();
        try{
            byte[] body = message.getBody();
            OrderCreateMsg createMsg = JSONs.toObj(new String(body), OrderCreateMsg.class);
            //执行业务 关闭订单
            log.info("关闭订单");
            orderBizService.closeOrder(createMsg.getOrderId(),createMsg.getUserId());
            channel.basicAck(properties.getDeliveryTag(),false);
        }catch (Exception e){
            log.error("MQ业务处理失败：{}",e);
            channel.basicNack(properties.getDeliveryTag(),false,true);
        }
    }

}
