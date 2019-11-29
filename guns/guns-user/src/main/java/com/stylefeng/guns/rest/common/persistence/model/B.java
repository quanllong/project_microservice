package com.stylefeng.guns.rest.common.persistence.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class B implements Serializable {
    private static final long serialVersionUID = 2915063455008561251L;//这里是idea随机生成的

    private String name;

    private Integer age;
}
