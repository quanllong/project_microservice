package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UserModifyVo {
    int status;
    UserData data;
    
    @Data
    public static class UserData{
        int id;
        String username;
        String nickname;
        String email;
        String phone;
        int sex;
        String birthday;
        int lifeState;
        String biography;
        String address;
        String headAddress;
        private Date beginTime;
        private Date updateTime;
    }
}
