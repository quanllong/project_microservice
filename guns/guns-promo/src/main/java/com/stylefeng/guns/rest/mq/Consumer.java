package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;

import java.util.HashMap;
import java.util.List;


public class Consumer {

    public static void main(String[] args) throws MQClientException {

        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("consumer_group");

        consumer.setNamesrvAddr("localhost:9876");

        consumer.subscribe("17thTest","*"); // subExpression 表示tag

        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                //获取消息
                MessageExt messageExt = msgs.get(0);
                byte[] body = messageExt.getBody();

                String bodyStr = new String(body);

//                System.out.println(bodyStr);
                HashMap hashMap = JSON.parseObject(bodyStr, HashMap.class);
                System.out.println("hashMap = " + hashMap);

                Gson gson = new Gson();
                HashMap hashMap1 = gson.fromJson(bodyStr, HashMap.class);
                System.out.println("hashMap1 = " + hashMap1);

                Integer id = (Integer) hashMap.get("id");

                System.out.println("从消息中取到的id是："+id);


                //有重试机制 16次

                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();

    }
}
