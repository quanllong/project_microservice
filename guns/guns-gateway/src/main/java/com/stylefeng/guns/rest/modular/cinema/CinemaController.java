package com.stylefeng.guns.rest.modular.cinema;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.service.CinemaService;
import com.stylefeng.guns.rest.service.vo.CinemaVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.CinemaInfoVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.FieldReqVO4Field;
import com.stylefeng.guns.rest.service.vo.cinemavo.FilmInfoVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.HallInfoVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;

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
    public FieldReqVO4Field getField(Integer cinemaId){
        // 获取影院信息
        CinemaInfoVO cinemaInfo = cinemaService.getCinemaInfoById(cinemaId);
        // 获取该影院正在上映的电影
        List<FilmInfoVO> filmList = cinemaService.getFilmInfoByCinemaId(cinemaId);

        HashMap<String, Object> map = new HashMap<>();
        map.put("cinemaInfo",cinemaInfo);
        map.put("filmList",filmList);
        return FieldReqVO4Field.ok(map);
    }

    /*
    Request URL: http://115.29.141.32/cinema/getFieldInfo?cinemaId=6&fieldId=7
    Request Method: POST
     */
    @RequestMapping(value = "getFieldInfo")
    public FieldReqVO4Field getFieldInfo(Integer cinemaId, Integer fieldId){
        /*Integer cinemaId = (Integer) map.get("cinemaId");
        Integer fieldId = (Integer) map.get("fieldId");*/
        if (cinemaId == null || fieldId == null){
            return null;
        }
        // 获取影院信息
        CinemaInfoVO cinemaInfoVO = cinemaService.getCinemaInfoById(cinemaId);

        // 根据放映场次id获取放映信息
        HallInfoVO hallInfoVO = cinemaService.getFilmFieldInfo(fieldId);

        // 根据放映场次查询播放的电影编号，然后根据电影编号获取对应的电影信息
        FilmInfoVO filmInfoVO = cinemaService.getFilmInfoByFieldId(fieldId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("cinemaInfo",cinemaInfoVO);
        hashMap.put("filmInfo",filmInfoVO);
        hashMap.put("hallInfo",hallInfoVO);
        return FieldReqVO4Field.ok(hashMap);
    }
}
