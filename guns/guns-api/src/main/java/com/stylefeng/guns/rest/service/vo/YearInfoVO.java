package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author:ys
 * @Date:2019/11/29
 * @time:15:35
 */
@Data
public class YearInfoVO implements Serializable {

    private static final long serialVersionUID = -2698146257337908266L;

    private String yearId;

    private String yearName;

    private Boolean isActive = false;
}
