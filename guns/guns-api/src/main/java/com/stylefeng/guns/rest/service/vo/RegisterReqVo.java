package com.stylefeng.guns.rest.service.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class RegisterReqVo implements Serializable {

    private static final long serialVersionUID = 807997771751451446L;

    private String username;

    private String password;

    private String email;

    private String mobile;

    private String address;
}
