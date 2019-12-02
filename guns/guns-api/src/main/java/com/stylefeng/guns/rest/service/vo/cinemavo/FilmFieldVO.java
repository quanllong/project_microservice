package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;
@Data
public class FilmFieldVO implements Serializable {
    private static final long serialVersionUID = -6025726186318419601L;
    private String fieldId;     // field表的最左侧uuid
    private String beginTime;
    private String endTime;
    private String language;
    private String hallName;
    private String price;
}
