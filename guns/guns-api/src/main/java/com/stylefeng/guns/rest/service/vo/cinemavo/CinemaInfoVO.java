package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;
@Data
public class CinemaInfoVO implements Serializable {
    private static final long serialVersionUID = -8650078589498207757L;
    private Integer cinemaId;
    private String cinemaAdress;    // 响应报文使用的是Adress
    private String cinemaName;
    private String cinemaPhone;
    private String imgUrl;
}
