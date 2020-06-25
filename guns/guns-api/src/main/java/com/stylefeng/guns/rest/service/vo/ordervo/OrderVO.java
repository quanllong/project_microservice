package com.stylefeng.guns.rest.service.vo.ordervo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderVO implements Serializable {
    private static final long serialVersionUID = -1330832994860469750L;
    private String orderId;
    private String filmName;
    private String fieldTime;
    private String cinemaName;
    private String seatsName;
    private String orderPrice;
    private Long orderTimestamp;
    private String orderStatus;
}
