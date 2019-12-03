package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.alipay.demo.trade.Main;
import com.aliyun.oss.OSSClient;
import com.cskaoyan.component.AliyunComponent;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.PayService;
import com.stylefeng.guns.rest.service.vo.CodeInfo;
import com.stylefeng.guns.rest.service.vo.MainInfo;
import com.stylefeng.guns.rest.service.vo.payvo.PayInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Service(interfaceClass = PayService.class,timeout = 1200000)
public class PayServiceImpl implements PayService {

    @Autowired
    Main main;
    @Autowired
    AliyunComponent aliyunComponent;


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
            return codeInfo;
        }

        String url = "http://" + aliyunComponent.getOss().getBucket() + "." + aliyunComponent.getOss().getEndPoint() + "/";

        // 上传二维码
        File file = new File(filePath);
        ossClient.putObject(aliyunComponent.getOss().getBucket(), fileName, file);

        // 返回url以及filmName
        codeInfo.setQRCodeAddress(fileName);
        codeInfo.setImgPre(url);
        return codeInfo;
    }
}
