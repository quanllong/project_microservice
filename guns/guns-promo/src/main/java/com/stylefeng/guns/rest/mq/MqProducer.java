package com.stylefeng.guns.rest.mq;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.stylefeng.guns.rest.consistent.StockLogStatus;
import com.stylefeng.guns.rest.modular.promo.bean.ArgsBean;
import com.stylefeng.guns.rest.modular.promo.bean.MsgBean;
import com.stylefeng.guns.rest.service.PromoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.*;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
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

    private TransactionMQProducer txProducer;

    @Autowired
    MtimeStockLogMapper mtimeStockLogMapper;

    @Reference(interfaceClass = PromoService.class,check = false)
    private PromoService promoService;

    @Value("${mq.nameserver.addr}")
    private String addr;

    @Value("${mq.topic}")
    private String topic;

    @Value("${mq.producer-group}")
    private String producerGroup;

    @Value("${mq.transaction-producer-group}")
    private String transactionProducerGroup;

    @PostConstruct
    public void init(){

        producer = new DefaultMQProducer(producerGroup);
        producer.setNamesrvAddr(addr);
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        log.info("producer 初始化成功!!! addr:{}",addr);

        txProducer = new TransactionMQProducer(transactionProducerGroup);
        txProducer.setNamesrvAddr(addr);
        try {
            txProducer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
        log.info("txProducer初始化成功 addr:{}",addr);

        // 设置一个事务监听器
        txProducer.setTransactionListener(new TransactionListener() {
            /**
             * 监听到发送消息成功就会执行本地事务
             */
            @Override
            public LocalTransactionState executeLocalTransaction(Message message, Object args) {
                ArgsBean argsBean = (ArgsBean) args;
                Integer userId = argsBean.getUserId();
                String amount = argsBean.getAmount();
                String promoId = argsBean.getPromoId();
                String stockLogId = argsBean.getStockLogId();

                // 执行本地事务
                boolean flag = false;
                try{
                    flag = promoService.saveOrderInfo(promoId, amount, userId,stockLogId);
                } catch (Exception e){
                    e.printStackTrace();
                }

                if (!flag){
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.COMMIT_MESSAGE;
            }

            @Override
            public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
                // 这个方法什么时候被调用
                byte[] body = messageExt.getBody();
                String s = new String(body);
                MsgBean msgBean = JSON.parseObject(s, MsgBean.class);

                String stockLogId = msgBean.getStockLogId();
                MtimeStockLog stockLog = mtimeStockLogMapper.selectById(stockLogId);
                if(stockLog.getStatus() == StockLogStatus.ORDER_SUCCESS.getStatus()){
                    return LocalTransactionState.COMMIT_MESSAGE;
                }
                if(stockLog.getStatus() == StockLogStatus.ORDER_FAIL.getStatus()){
                    return LocalTransactionState.ROLLBACK_MESSAGE;
                }
                return LocalTransactionState.UNKNOW;
            }
        });
    }

    /*public Boolean decreaseStock(Integer promoId, Integer stock){

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

    }*/


    public Boolean savePromoInfoInTransaction(String promoId, String amount, Integer userId,String stockLogId) {

        // 封装要发送的消息，
        // consumer的任务是更新数据库的库存，它需要promoId以及减去的amount而已
        MsgBean msgBean = new MsgBean();
        msgBean.setAmount(Integer.valueOf(amount));
        msgBean.setPromoId(promoId);
        String jsonString = JSON.toJSONString(msgBean);

        Message message = new Message(topic, jsonString.getBytes(Charset.forName("utf-8")));

        // 封装形参,提供给本地事务
        ArgsBean argsBean = new ArgsBean();
        argsBean.setAmount(amount);
        argsBean.setPromoId(promoId);
        argsBean.setUserId(userId);
        argsBean.setStockLogId(stockLogId);

        TransactionSendResult transactionSendResult = null;
        try {
            transactionSendResult = txProducer.sendMessageInTransaction(message,argsBean);
        } catch (MQClientException e) {
            e.printStackTrace();
            log.info("发送消息异常，可能是内存不足");
        }

        if (transactionSendResult == null){
            return false;
        }

        // 获取本地事务执行状态
        LocalTransactionState localTransactionState = transactionSendResult.getLocalTransactionState();
        if(LocalTransactionState.COMMIT_MESSAGE.equals(localTransactionState)){
            return true;
        } else {
            return false;
        }
    }
}
