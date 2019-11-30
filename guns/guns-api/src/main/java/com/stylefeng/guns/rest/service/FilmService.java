package com.stylefeng.guns.rest.service;


import com.stylefeng.guns.rest.service.vo.FilmRequestVO;
import com.stylefeng.guns.rest.service.vo.FilmVO;

import java.util.Map;


public interface FilmService {
    FilmVO selectById(Integer id);

    Map getHotFilms(FilmRequestVO filmRequestVO);

    Map getSoonFilms(FilmRequestVO filmRequestVO);

    Map getClassicFilms(FilmRequestVO filmRequestVO);

    Map films(Integer UUID);

    Map selectByKw(String kw);
}
