package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author:ys
 * @Date:2019/11/29
 * @time:15:33
 */
@Data
public class SourceInfoVO implements Serializable {

    private static final long serialVersionUID = 857034473002436057L;

    private String sourceId;

    private String sourceName;

    private Boolean isActive = false;
}
