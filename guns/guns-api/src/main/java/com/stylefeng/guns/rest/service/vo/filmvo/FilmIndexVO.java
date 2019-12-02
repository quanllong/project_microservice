package com.stylefeng.guns.rest.service.vo.filmvo;

import lombok.Data;

import java.util.List;

/**
 * @Author:ys
 * @Date:2019/11/28
 * @time:21:35
 */
@Data
public class FilmIndexVO {
    private List<BannersVO> banners;

    private FilmsVO hotFilms;

    private FilmsVO soonFilms;

    private List<FilmInfoVO> boxRanking;

    private List<FilmInfoVO> expectRanking;

    private List<FilmInfoVO> top100;
}
