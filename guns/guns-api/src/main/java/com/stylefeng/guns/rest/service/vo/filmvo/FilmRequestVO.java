package com.stylefeng.guns.rest.service.vo.filmvo;

import lombok.Data;

import java.io.Serializable;

@Data
public class FilmRequestVO implements Serializable {

    private static final long serialVersionUID = -4904247951680571991L;
    private Integer showType;
    private Integer sortId;
    private Integer sourceId;
    private Integer catId;
    private Integer yearId;
    private Integer nowPage;
    private Integer pageSize;
    private String kw;
}
