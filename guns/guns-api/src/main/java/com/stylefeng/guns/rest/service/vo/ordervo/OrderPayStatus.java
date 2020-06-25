package com.stylefeng.guns.rest.service.vo.ordervo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPayStatus implements Serializable {
    private static final long serialVersionUID = 4705718820914267722L;
    String orderId;
    Integer status;

    public OrderPayStatus() {
    }

    public OrderPayStatus(String orderId, Integer status) {
        this.orderId = orderId;
        this.status = status;
    }
}
