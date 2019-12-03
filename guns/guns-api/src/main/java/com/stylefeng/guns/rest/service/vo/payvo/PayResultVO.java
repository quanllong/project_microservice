package com.stylefeng.guns.rest.service.vo.payvo;

import lombok.Data;

import java.io.Serializable;

@Data
public class PayResultVO implements Serializable {
    private static final long serialVersionUID = 6428774166795923939L;
    String orderId;
    Integer orderStatus;
    String orderMsg;

    Integer status;
    String msg;

}
