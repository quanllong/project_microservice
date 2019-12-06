package com.stylefeng.guns.rest.modular.promo.bean;

import lombok.Data;

/**
 * 用来封装消息
 * quanllong
 */
@Data
public class MsgBean {
    String promoId;
    String amount;
    Integer userId;
    String stockLogId;
}
