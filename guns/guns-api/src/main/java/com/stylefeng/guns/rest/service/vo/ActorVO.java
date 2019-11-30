package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;
@Data
public class ActorVO implements Serializable {
    private static final long serialVersionUID = 8882315396375985887L;
    private String directorName;
    private String imgAddress;
    private String roleName;
}
