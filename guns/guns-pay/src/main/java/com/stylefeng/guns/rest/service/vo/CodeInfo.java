package com.stylefeng.guns.rest.service.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * 封装getCode方法的返回值
 */
@Data
public class CodeInfo {
    String imgPre;
    String qRCodeAddress;
}
