package com.stylefeng.guns.rest.service.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author:ys
 * @Date:2019/11/28
 * @time:21:19
 */


//此bean为HotFilms he SoonFilms的bean
@Data
public class FilmsVO implements Serializable {

    private static final long serialVersionUID = 5017238990975451997L;

    private Integer filmNum;

    private List<FilmInfoVO> filmInfo;

    private int page;

    private int totalPage;
}
