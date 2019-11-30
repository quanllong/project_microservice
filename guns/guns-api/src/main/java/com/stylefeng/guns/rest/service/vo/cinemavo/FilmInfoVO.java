package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class FilmInfoVO implements Serializable {
    private static final long serialVersionUID = -663692446515507878L;
    private String filmId;
    private String filmName;
    private String filmLength;
    private String filmType;
    private String filmCats;
    private String actors;
    private String imgAddress;
    private List<FilmFieldVO> fileFields;
}
