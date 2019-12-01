package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.*;

import java.util.List;

/**
 * @Author:ys
 * @Date:2019/11/28
 * @time:20:44
 */
public interface FilmService {

    //banners
    List<BannersVO> getBanners();

    //hotFilms
    FilmsVO getHotFilms(boolean isLimit, int nums);

    //soonFilms
    FilmsVO getSoonFilms(boolean isLimit, int nums);

    //boxRanking
    List<FilmInfoVO> getBoxRanking(Integer count);

    //expectRanking
    List<FilmInfoVO> getExpectRanking(Integer count);

    //top100
    List<FilmInfoVO> getTop100(Integer count);

    //cat
    List<CatInfoVO> getCatInfo(Integer catId);

    List<SourceInfoVO> getsourceInfo(Integer sourceId);

    List<YearInfoVO> getyearInfo(Integer yearId);



}
