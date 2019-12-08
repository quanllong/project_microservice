package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.demo.trade.Main;
import com.aliyun.oss.OSSClient;
import com.cskaoyan.component.AliyunComponent;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.consistant.OrderStatus;
import com.stylefeng.guns.rest.consistant.RedisPrefixConsistant;
import com.stylefeng.guns.rest.service.PayService;
import com.stylefeng.guns.rest.service.vo.CodeInfo;
import com.stylefeng.guns.rest.service.vo.MainInfo;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderPayStatus;
import com.stylefeng.guns.rest.service.vo.payvo.PayInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Service(interfaceClass = PayService.class,timeout = 1200000)
public class PayServiceImpl implements PayService {

    @Autowired
    Main main;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    AliyunComponent aliyunComponent;
    @Autowired
    MoocOrderTMapper moocOrderTMapper;


    @Override
    public PayInfo getQRCodeAddress(String orderId) {

        // 得到url以及filmName
        CodeInfo codeInfo = getCode(orderId);
        PayInfo payInfo = new PayInfo();

        if(codeInfo == null){
            payInfo.setImgPre("0000");
            payInfo.setQRCodeAddress("0001");
            return payInfo;
        }
        payInfo.setImgPre(codeInfo.getImgPre());
        payInfo.setQRCodeAddress(codeInfo.getQRCodeAddress());
        return payInfo;
    }

    /**
     * 取得二维码的阿里云地址
     * @param orderId
     * @return
     */
    public CodeInfo getCode(String orderId){

        OSSClient ossClient = aliyunComponent.getOssClient();

        // 返回的mainInfo装着filePath和fileName，filePath是本地磁盘路径，用来给阿里云上传
        MainInfo mainInfo = main.generateCode(orderId);

        if(mainInfo == null){
            System.out.println("PayService : mainInfo is null");
            return null;
        }
        String fileName = mainInfo.getFileName();
        String filePath = mainInfo.getFilePath();
        CodeInfo codeInfo = new CodeInfo();
        if("fileNameNotCreated".equals(fileName) || "filePathNotCreated".equals(filePath)){
            codeInfo.setImgPre("imgPreNotCreated");
            codeInfo.setQRCodeAddress("qRCodeAddressNotCreated");
           /* codeInfo.setImgPre("localhost/pic/");
            codeInfo.setQRCodeAddress("1.png");*/     // localhost:8080/pic/1.png
            return codeInfo;
        }

        String url = "http://" + aliyunComponent.getOss().getBucket() + "." + aliyunComponent.getOss().getEndPoint() + "/";

        // 上传二维码
        File file = new File(filePath);
        ossClient.putObject(aliyunComponent.getOss().getBucket(), fileName, file);

        // 返回url以及filmName
        codeInfo.setQRCodeAddress(fileName);
        codeInfo.setImgPre(url);
       /* codeInfo.setImgPre("localhost:8080/pic/");
        codeInfo.setQRCodeAddress("1.png");*/     // localhost:8080/pic/1.png
        return codeInfo;
    }

    /**
     * 检查秒杀订单是否已经支付
     * @param orderId
     * @return
     */
    @Override
    public int checkPayStatus(String orderId,Integer userId) {

        // 查看订单有没有被超时关闭
        String key = String.format(RedisPrefixConsistant.CURRENT_ORDER,userId);
        OrderPayStatus orderPayStatus = (OrderPayStatus) redisTemplate.opsForValue().get(key);
        if(orderPayStatus != null){
            if (OrderStatus.CLOSED.getCode().equals(orderPayStatus.getStatus())) {
                return OrderStatus.CLOSED.getCode();
            }
        }

        // 查二维码有没有被扫,没有扫则返回false
        boolean b = main.tradeQuery(orderId);
        if(b){
            return OrderStatus.PAY_SUCCESS.getCode();
        } else {
            return OrderStatus.NOT_PAY.getCode();
        }
    }

    @Override
    public int updateOrderStatus(String orderId, Integer userId, int status) {
        // 更新数据库
        int update = moocOrderTMapper.updateStatusByUuid(orderId,status);

        // 更新redis
        if (update == 1){
            String key = String.format(RedisPrefixConsistant.CURRENT_ORDER,userId);
            OrderPayStatus orderPayStatus = (OrderPayStatus) redisTemplate.opsForValue().get(key);
            orderPayStatus.setStatus(OrderStatus.PAY_SUCCESS.getCode());
            redisTemplate.opsForValue().set(key,orderPayStatus);
        }
        return update;
    }
}
