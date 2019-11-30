package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.CinemaService;
import com.stylefeng.guns.rest.service.vo.CinemaVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.CinemaInfoVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.FieldReqVo;
import com.stylefeng.guns.rest.service.vo.cinemavo.FilmInfoVO;
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

    @RequestMapping("query/cinema")
    public CinemaVO query(Integer id){
        CinemaVO cinemaVO = cinemaService.selectById(id);
        return cinemaVO;
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
