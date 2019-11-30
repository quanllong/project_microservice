package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;

@Data
public class BrandVo implements Serializable {
    private static final long serialVersionUID = -779964944152158736L;
    /*	“brandId”: 1,
        “brandName”:”全部”,
        “active”:true*/
    private Integer brandId;
    private String brandName;
    private boolean active=false;

}
