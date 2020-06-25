package com.stylefeng.guns.rest.service.vo.promovo;

import lombok.Data;

import java.io.Serializable;

/**
 * 接收前端传来的查询条件
 */
@Data
public class PromoParams implements Serializable {
    private static final long serialVersionUID = -466062951814770487L;
    Integer brandId;
    String hallType;
    Integer areaId;
    Integer pageSize;
    Integer nowPage;
}
