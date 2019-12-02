package com.stylefeng.guns.rest.service.vo.orderys;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author:ys
 * @Date:2019/11/30
 * @time:15:37
 */
@Data
public class OrderVOYs implements Serializable {

    private static final long serialVersionUID = 930475900708254030L;

    private String OrderId;

    private String filmName;

    private String cinemaName;

    private String seatsName;

    private String orderPrice;

    private String orderTimestamp;

    private String orderStatus;

    private String fieldTime;

    //影院信息
    private String cinemaId;

    private String filmPrice;


}
