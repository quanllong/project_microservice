package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaVO implements Serializable {
    private static final long serialVersionUID = -5098491409901581820L;
    String cinemaName;
    Integer uuid;
}
