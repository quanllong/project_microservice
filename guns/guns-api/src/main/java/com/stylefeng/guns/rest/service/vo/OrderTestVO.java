package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderTestVO implements Serializable {
    private static final long serialVersionUID = -4646189550752425803L;
    // 这个类用来测试项目跑不跑的起来，不要删。quanllong
    String uuid;
    String seatsName;
}
