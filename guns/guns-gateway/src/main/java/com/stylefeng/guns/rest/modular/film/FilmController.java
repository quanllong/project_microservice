package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.FilmService;
import com.stylefeng.guns.rest.service.vo.FilmInfoVO;
import com.stylefeng.guns.rest.service.vo.FilmRequestVO;
import com.stylefeng.guns.rest.service.vo.FilmVO;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class FilmController {

    @Reference(interfaceClass = FilmService.class,check = false)
    private FilmService filmService;

    @RequestMapping("film/query")
    public FilmVO query(Integer id){
        FilmVO filmVO = filmService.selectById(id);
        return filmVO;
    }

    @RequestMapping(value = "film/getFilms")
    public BaseReqVo getFilms(FilmRequestVO filmRequestVO){
        BaseReqVo baseReqVo = new BaseReqVo();
        Map map = new HashMap();
        if(filmRequestVO.getShowType()==1){
            map = filmService.getHotFilms(filmRequestVO);

        }else if(filmRequestVO.getShowType()==2){
            map = filmService.getSoonFilms(filmRequestVO);

        }else if(filmRequestVO.getShowType()==3){
            map = filmService.getClassicFilms(filmRequestVO);

        }else if(filmRequestVO.getKw()!=null){
            map = filmService.selectByKw(filmRequestVO.getKw());
        }
        if((int)map.get("totalPage")==0){
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("查询失败，无banner可加载");
            return baseReqVo;
        }
        if(map!=null&&map.size()!=0){
            baseReqVo.setStatus(0);
            baseReqVo.setImgPre("http://img.meetingshop.cn/");
            baseReqVo.setNowPage(filmRequestVO.getNowPage());
            baseReqVo.setTotalPage((int)map.get("totalPage"));
            baseReqVo.setData(map.get("data"));
            return baseReqVo;
        }else {
            baseReqVo.setStatus(999);
            baseReqVo.setMsg("系统出现异常，请联系管理员");
            return baseReqVo;
        }
    }

    @RequestMapping(value = "film/films/{UUID}")
    //如果占位符字段和参数字段相同就不需要写@param
    public BaseReqVo films(@PathVariable Integer UUID){
        Map film = filmService.films(UUID);
        BaseReqVo baseReqVo = new BaseReqVo();
        if(film!=null) {
            baseReqVo.setData(film);
            baseReqVo.setImgPre("http://img.meetingshop.cn/");
            baseReqVo.setStatus(0);
        }else {
            baseReqVo.setStatus(999);
            baseReqVo.setMsg("系统出现异常，请联系管理员");
        }
        return baseReqVo;
    }
}
