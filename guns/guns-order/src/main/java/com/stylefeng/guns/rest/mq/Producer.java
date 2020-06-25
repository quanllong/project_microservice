package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;

@Component
@Slf4j
public class Producer {

    @Value("${mq.nameserver.addr}")
    private String addr;

    @Value("${mq.topic2}")
    private String topic;

    @Value("${mq.producer-group}")
    private String group;

    @Value("${mq.time-level}")
    private Integer timeLevel;

    private DefaultMQProducer producer;

    @PostConstruct
    public void init(){
        // 只创建一次，否则会报异常
        producer = new DefaultMQProducer(group);
        producer.setNamesrvAddr(addr);

        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("producer启动失败！");
        }
    }


    public SendResult sendDelayOrder(String orderId, Integer userId)  {

        HashMap<String,Object> hashMap = new HashMap<>();

        hashMap.put("orderId",orderId);
        hashMap.put("userId",userId);
        String body = JSON.toJSONString(hashMap);

        // 要指定发送到哪一个topic
        Message message = new Message(topic, body.getBytes(Charset.forName("utf-8")));

        // 延时的级别为3 对应的时间为10s 就是发送后延时10S在把消息投递出去
        // messageDelayLevel=1s 5s 10s 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        message.setDelayTimeLevel(timeLevel);

        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("发送延时订单失败，orderId:{}",orderId);
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // System.out.println(JSON.toJSONString(sendResult));
        return sendResult;
    }
}
