package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;

@Data
public class AreaVo implements Serializable {
    private static final long serialVersionUID = -6552221328106413531L;
    private Integer areaId;
    private String areaName;
    private boolean active=false;
}
