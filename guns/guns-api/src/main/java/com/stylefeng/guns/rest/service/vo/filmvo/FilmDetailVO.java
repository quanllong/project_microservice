package com.stylefeng.guns.rest.service.vo.filmvo;

import lombok.Data;

import java.io.Serializable;
@Data
public class FilmDetailVO implements Serializable {
    private static final long serialVersionUID = -5746112834466361053L;
    private Integer filmId;
    private String filmName;
    private String imgAddress;
    private String filmScore;
    private Integer fileType;
}
