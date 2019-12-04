package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.HashMap;


@Component
@Slf4j
public class MqProducer {

    private DefaultMQProducer producer;

    @Value("${mq.nameserver.addr}")
    private String addr;

    @Value("${mq.topic}")
    private String topic;

    @PostConstruct
    public void init(){

        producer = new DefaultMQProducer("producer_group");
        producer.setNamesrvAddr(addr);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        log.info("producer 初始化成功!!! addr:{}",addr);
    }

    public Boolean decreaseStock(Integer promoId, Integer stock){

        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put("promoId",promoId);
        hashMap.put("amount",stock);

        Message message = new Message(topic, JSON.toJSONString(hashMap).getBytes(Charset.forName("utf-8")));
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
        } catch (MQClientException e) {
            e.printStackTrace();
        } catch (RemotingException e) {
            e.printStackTrace();
        } catch (MQBrokerException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        log.info("发送消息完成，发送结果：{}", JSON.toJSONString(sendResult));
        if (sendResult == null) {
            return false;
        }
        else
        {
            SendStatus sendStatus = sendResult.getSendStatus();
            if (SendStatus.SEND_OK.equals(sendStatus)){
                return true;
            }
            return false;
        }

    }
}
