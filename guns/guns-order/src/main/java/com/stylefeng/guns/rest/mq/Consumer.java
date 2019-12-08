package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.stylefeng.guns.rest.consistant.OrderStatus;
import com.stylefeng.guns.rest.consistant.RedisPrefixConsistant;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderPayStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class Consumer {

    @Value("${mq.nameserver.addr}")
    private String nameserverAddr;

    @Value("${mq.topic2}")
    private String topic;

    @Value("${mq.consumer-group}")
    private String group;

    @Autowired
    OrderService orderService;

    @Autowired
    RedisTemplate redisTemplate;

    private DefaultMQPushConsumer consumer ;

    @PostConstruct
    public void init(){
        consumer = new DefaultMQPushConsumer(group);

        consumer.setNamesrvAddr(nameserverAddr);

        try {
            consumer.subscribe(topic,"*"); // subExpression 表示tag
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("获取消息失败");
        }

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                //获取消息
                MessageExt messageExt = msgs.get(0);
                byte[] body = messageExt.getBody();

                String bodyStr = new String(body);

                HashMap hashMap = JSON.parseObject(bodyStr, HashMap.class);

                /*Gson gson = new Gson();
                HashMap hashMap1 = gson.fromJson(bodyStr, HashMap.class);
                System.out.println("hashMap1 = " + hashMap1);*/

                // String orderId, Integer userId
                // 订单Id
                String orderId = null;
                Integer userId = null;
                try{
                    orderId = (String) hashMap.get("orderId");
                    userId = (Integer) hashMap.get("userId");
                } catch (Exception e){
                    e.printStackTrace();
                    log.info("consumer类型转换异常");
                }

                // 检查订单是否已支付
                Boolean flag = orderService.checkOrdinaryOrderStatus(orderId);
                if(!flag){
                    int update = orderService.updateOrderStatus(orderId, OrderStatus.CLOSED.getCode());
                    if(update == 1){
                        log.info("订单已关闭，orderId:{}",orderId);
                    }
                    // 更新redis
                    String key = String.format(RedisPrefixConsistant.CURRENT_ORDER,userId);
                    OrderPayStatus orderPayStatus = new OrderPayStatus(orderId, OrderStatus.CLOSED.getCode());
                    redisTemplate.opsForValue().set(key,orderPayStatus);
                }

                //有重试机制 16次

                String key = String.format(RedisPrefixConsistant.CURRENT_ORDER,userId);
                OrderPayStatus orderPayStatus = new OrderPayStatus(orderId, OrderStatus.PAY_SUCCESS.getCode());
                redisTemplate.opsForValue().set(key,orderPayStatus);
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        try {
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("consumer启动失败");
        }
    }
}
