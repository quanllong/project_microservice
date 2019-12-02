package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmInfoVO implements Serializable {
    private static final long serialVersionUID = -4339768462969660445L;
    private String filmId;
    private String filmName;
    private String filmLength;
    private String filmType;
    private String filmCats;
    private String actors;
    private String imgAddress;
    private List<FilmFieldVO> filmFields;
}
