package com.stylefeng.guns.rest.service.vo.filmvo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:ys
 * @Date:2019/11/28
 * @time:21:07
 */
@Data
public class  FilmInfoVO implements Serializable {

    private static final long serialVersionUID = 3725705950132039464L;

    private String filmId;

    private Integer filmType;

    private String imgAddress;

    private String filmName;

    private String filmScore;

    private String score;

    private Integer expectNum;

    private String showTime;

    private Integer boxNum;

    private String filmLength;

    private String filmCats;

    private String actors;

    private String filmImgs;
}
