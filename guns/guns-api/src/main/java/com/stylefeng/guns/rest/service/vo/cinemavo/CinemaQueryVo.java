package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;

@Data
public class CinemaQueryVo implements Serializable {

    private static final long serialVersionUID = -3116902373875026819L;
    private Integer brandId=99;
    private Integer districtId=99;
    private Integer hallType=99;
    private Integer pageSize=12;
    private Integer nowPage=1;
    private Integer areaId=99;

}
