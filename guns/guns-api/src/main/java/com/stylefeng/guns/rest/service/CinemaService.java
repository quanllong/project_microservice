package com.stylefeng.guns.rest.service;


import com.stylefeng.guns.rest.service.vo.CinemaVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.*;

import java.util.List;

public interface CinemaService {
    CinemaVO selectById(Integer id);

    //影院接口1，yw
    List<CinemaVO> getCinemas(CinemaQueryVo cinemaQueryVo);




    List<BrandVo> selectBrandByBrandId(Integer brandId);

    List<AreaVo> selectAreaByBrandId(Integer areaId);

    List<HallTypeVo> selectHallByBrandType(Integer hallType);

    // 供接口3和4使用，cinemaInfo.根据影院编号，获取影院信息，quanllong.被两个接口所共用
    CinemaInfoVO getCinemaInfoById(Integer cinemaId);

    // 供接口3使用，获取所有电影的信息和对应的放映场次信息，根据电影编号，quanllong
    List<FilmInfoVO> getFilmInfoByCinemaId(Integer cinemaId);

    // 供接口4使用，filmInfo.根据放映场次id获得放映电影的信息，quanllong
    FilmInfoVO getFilmInfoByFieldId(Integer fieldId);

    // 供接口4使用，hallInfo.根据放映场次id获取放映信息
    HallInfoVO getFilmFieldInfo(Integer fieldId);
}
