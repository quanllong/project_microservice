package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HallInfoVO implements Serializable {
    private static final long serialVersionUID = -2587748318681089989L;
    private Integer hallFieldId;
    private String hallName;
    private String price;
    private String discountPrice;
    private String seatFile;
    // 已售座位必须关联订单才能查询
    private String soldSeats;

}
