package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author:ys
 * @Date:2019/11/29
 * @time:14:27
 */
@Data
public class CatInfoVO implements Serializable {

    private static final long serialVersionUID = 175218591121953665L;

    private String catId;

    private String catName;

    private Boolean isActive = false;
}
