package com.stylefeng.guns.rest.service.vo;




import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author:ys
 * @Date:2019/11/28
 * @time:21:07
 */
@Data
public class FilmInfoVO implements Serializable {

    private static final long serialVersionUID = 3725705950132039464L;

    private String filmId;

    private Integer filmType;

    private String imgAddress;

    private String filmName;

    private String filmScore;

    private Integer expectNum;

    private Date showTime;

    private Integer boxNum;

    private String filmImgs;
}
