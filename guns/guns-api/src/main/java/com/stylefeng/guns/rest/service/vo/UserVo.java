package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserVo implements Serializable {

    private static final long serialVersionUID = 1425871235633784903L;
    String actorName;
    Integer uuid;
}
