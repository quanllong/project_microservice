package com.stylefeng.guns.rest.service.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class GetUserInfoVo implements Serializable {

    private static final long serialVersionUID = -165427221898842473L;
    
    private String address;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date begainTime;
    
    private String biography;
    
    private String birthday;
    
    private String email;
    
    private String headAddress;
    
    private String lifeState;
    
    private String nickname;
    
    private String phone;
    
    private Integer sex;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    private Date updateTime;
    
    private String username;
    
    private int uuid;

}
