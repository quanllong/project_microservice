package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.filmvo.*;

import java.util.List;
import java.util.Map;


public interface FilmService {

    Map getHotFilms(FilmRequestVO filmRequestVO);

    Map getSoonFilms(FilmRequestVO filmRequestVO);

    Map getClassicFilms(FilmRequestVO filmRequestVO);

    Map films(Integer UUID);

    Map selectByKw(String kw);

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

    FilmVO selectById(Integer id);
}
