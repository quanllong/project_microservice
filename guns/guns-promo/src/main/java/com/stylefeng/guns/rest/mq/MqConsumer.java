package com.stylefeng.guns.rest.mq;

import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;


@Component
@Slf4j
public class MqConsumer {

    @Value("${mq.nameserver.addr}")
    private String addr;

    @Value("${mq.topic}")
    private String topic;

    @Autowired
    MtimePromoStockMapper mtimePromoStockMapper;

    private DefaultMQPushConsumer mqPushConsumer;

//    @Autowired
//    StockMapper mapper;

    @PostConstruct
    public void init() throws MQClientException {
        mqPushConsumer = new DefaultMQPushConsumer("consumer_group");
        mqPushConsumer.setNamesrvAddr(addr);

        mqPushConsumer.subscribe(topic,"*");

        mqPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {

                //获取消息
                MessageExt messageExt = msgs.get(0);
                byte[] body = messageExt.getBody();

                String bodyStr = new String(body);

//                System.out.println(bodyStr);
                HashMap hashMap = JSON.parseObject(bodyStr, HashMap.class);

                Integer promoId = (Integer)hashMap.get("promoId");
                Integer stock = (Integer) hashMap.get("stock");

                log.info("收到消息，promoId:{}， stock:{}",promoId,stock);

                //有重试机制 16次

                // 真正去减数据库的库存

                int update = mtimePromoStockMapper.updateStock(promoId, stock);
                if(update == 1){
                    log.info("更新库存成功：promoId:{}, stock:{}",promoId,stock);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });

        mqPushConsumer.start();

    }
}
