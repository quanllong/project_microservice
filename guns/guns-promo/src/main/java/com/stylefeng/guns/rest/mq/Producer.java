package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.nio.charset.Charset;
import java.util.HashMap;

public class Producer {

    public static void main(String[] args) throws InterruptedException, RemotingException, MQClientException, MQBrokerException {

        DefaultMQProducer producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr("localhost:9876");

        producer.start();

//        String body = "今天天气真好啊！！";
        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("id",1);
        String body = JSON.toJSONString(hashMap);

        Message message = new Message("17thTest", body.getBytes(Charset.forName("utf-8")));

        SendResult sendResult = producer.send(message);

        System.out.println(JSON.toJSONString(sendResult));

    }
}
