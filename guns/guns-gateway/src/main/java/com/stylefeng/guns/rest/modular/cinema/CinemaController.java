package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;


import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.CinemaService;
import com.stylefeng.guns.rest.service.vo.CinemaVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.*;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



@RestController
@RequestMapping("cinema")
public class CinemaController {

    @Reference(interfaceClass = CinemaService.class,check = false)
    CinemaService cinemaService;

    @RequestMapping("/query/cinema")
    public CinemaVO query(Integer id){
        CinemaVO cinemaVO = cinemaService.selectById(id);
        return cinemaVO;
    }


    //1、查询影院列表-根据条件查询所有影院
    @RequestMapping("/getCinemas")
    public BaseReqVo getCinemas(CinemaQueryVo cinemaQueryVo){

        BaseReqVo baseReqVo=new BaseReqVo();
        List<CinemaVO> cinemaVOList=cinemaService.getCinemas(cinemaQueryVo);
        baseReqVo.setData(cinemaVOList);
        baseReqVo.setTotalPage((cinemaVOList.size()%cinemaQueryVo.getPageSize())+1);
        baseReqVo.setNowPage(cinemaQueryVo.getNowPage());
        baseReqVo.setStatus(0);

        return baseReqVo;
    }

    //2、获取影院列表查询条件
    @RequestMapping("getCondition")
    public BaseReqVo getCondition( CinemaQueryVo cinemaQueryVo) {

        Integer areaId = cinemaQueryVo.getAreaId();
        Integer brandId = cinemaQueryVo.getBrandId();
        Integer hallType = cinemaQueryVo.getHallType();

        List<BrandVo> brandVoList = cinemaService.selectBrandByBrandId(brandId);
        List<AreaVo> areaVoList = cinemaService.selectAreaByBrandId(areaId);
        List<HallTypeVo> hallTypeVoList = cinemaService.selectHallByBrandType(hallType);


        BaseReqVo baseReqVo = new BaseReqVo();
        baseReqVo.setStatus(0);
        Map<String, Object> map = new HashMap<>();
        map.put("brandList", brandVoList);
        map.put("areaList", areaVoList);
        map.put("halltypeList", hallTypeVoList);

        baseReqVo.setData(map);

        return baseReqVo;
    }
    /*
    Request URL: http://115.29.141.32/cinema/getFields?cinemaId=1
    Request Method: GET
     */
    @RequestMapping(value = "getFields",method = RequestMethod.GET)
    public FieldReqVo getField(Integer cinemaId){
        // 获取影院信息
        CinemaInfoVO cinemaInfo = cinemaService.getCinemaInfoById(cinemaId);
        // 获取该影院正在上映的电影
        List<FilmInfoVO> filmList = cinemaService.getFilmInfoByCinemaId(cinemaId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("cinemaInfo",cinemaInfo);
        map.put("filmList",filmList);
        return FieldReqVo.ok(map);
    }
}
