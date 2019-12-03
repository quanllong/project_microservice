package com.stylefeng.guns.rest.service.vo.payvo;

import lombok.Data;

import java.io.Serializable;
@Data
public class PayResultVO implements Serializable {
    private static final long serialVersionUID = 4094508425487995660L;
    /**
     * status : 0
     * data : {"orderId":"1234123","orderStatus":1,"orderMsg":"支付成功"}
     */
    private String msg;
    private int status;
    private Object data;
}
