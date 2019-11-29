package com.stylefeng.guns.rest.common.persistence.beanvo;

import lombok.Data;

@Data
public class RegisterReqVo {

    private String username;

    private String password;

    private String email;

    private String mobile;

    private String address;
}
