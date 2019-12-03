package com.stylefeng.guns.rest.service.vo.payvo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayInfo implements Serializable {
    private static final long serialVersionUID = 4108358436571581108L;
    private String qRCodeAddress;   // 二维码名字
    private String imgPre;  // 阿里云站点
}
