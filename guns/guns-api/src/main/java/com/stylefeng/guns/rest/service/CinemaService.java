package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.cinemavo.AreaVo;
import com.stylefeng.guns.rest.service.vo.cinemavo.BrandVo;
import com.stylefeng.guns.rest.service.vo.CinemaVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.CinemaQueryVo;
import com.stylefeng.guns.rest.service.vo.cinemavo.HallTypeVo;

import java.util.List;

public interface CinemaService {
    CinemaVO selectById(Integer id);

    //影院接口1，yw
    List<CinemaVO> getCinemas(CinemaQueryVo cinemaQueryVo);




    List<BrandVo> selectBrandByBrandId(Integer brandId);

    List<AreaVo> selectAreaByBrandId(Integer areaId);

    List<HallTypeVo> selectHallByBrandType(Integer hallType);

}
