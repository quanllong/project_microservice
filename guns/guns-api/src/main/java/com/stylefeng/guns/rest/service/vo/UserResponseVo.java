package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserResponseVo implements Serializable {
    private static final long serialVersionUID = -2045969488286119043L;
   
    int status;
   
    GetUserInfoVo data;
   
    private String msg;

    public static UserResponseVo fail(String s) {
        UserResponseVo userResponseVo = new UserResponseVo();
        userResponseVo.setStatus(1);
        userResponseVo.setMsg(s);
        return  userResponseVo;
    }
}