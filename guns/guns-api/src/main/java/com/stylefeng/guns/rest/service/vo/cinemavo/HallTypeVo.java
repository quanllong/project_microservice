package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;

@Data
public class HallTypeVo implements Serializable {
    private static final long serialVersionUID = -6768882776448908547L;
    private Integer halltypeId;
    private String halltypeName;
    private boolean active=false;
}
