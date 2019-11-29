package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.cinemavo.CinemaInfoVO;
import com.stylefeng.guns.rest.service.vo.CinemaVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.FilmInfoVO;

import java.util.HashMap;
import java.util.List;

public interface CinemaService {
    CinemaVO selectById(Integer id);

    // 根据影院编号，获取影院信息
    CinemaInfoVO getCinemaInfoById(Integer cinemaId);

    // 获取所有电影的信息和对应的放映场次信息，根据电影编号
    List<FilmInfoVO> getFilmInfoByCinemaId(Integer cinemaId);
}
