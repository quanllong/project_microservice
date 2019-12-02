package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.FilmService;
import com.stylefeng.guns.rest.service.vo.filmvo.*;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@RequestMapping("film")
public class FilmController {

    private String imgPre = "http://img.meetingshop.cn/";

    @Reference(interfaceClass = FilmService.class,check = false)
    FilmService filmService;

    /**
     * get请求
     * 首页显示
     * @return
     */
    @RequestMapping("getIndex")
    public BaseReqVo showHomePage(){
        FilmIndexVO filmIndexVO = null;
        try {
            //把返回的接口封装在FilmIndexVO里面作为响应体进行返回。
            List<BannersVO> banners = filmService.getBanners();
            FilmsVO hotFilms = filmService.getHotFilms(true, 8);
            FilmsVO soonFilms = filmService.getSoonFilms(true, 8);
            List<FilmInfoVO> boxRanking = filmService.getBoxRanking(9);
            List<FilmInfoVO> expectRanking = filmService.getExpectRanking(7);
            List<FilmInfoVO> top100 = filmService.getTop100(9);
            filmIndexVO = new FilmIndexVO();
            filmIndexVO.setBanners(banners);
            filmIndexVO.setHotFilms(hotFilms);
            filmIndexVO.setSoonFilms(soonFilms);
            filmIndexVO.setBoxRanking(boxRanking);
            filmIndexVO.setExpectRanking(expectRanking);
            filmIndexVO.setTop100(top100);
//            int i = 8/0;
        } catch (Exception e) {
            log.info("请求首页异常信息",e);
            return BaseReqVo.fail("首页接口异常");
        }
        //BaseReqVo响应体有问题,后面需要按照业务需求进行增加
        return BaseReqVo.ok(filmIndexVO,imgPre);
    }

    /**
     * get请求
     * 影片条件列表查询
     * @return
     */
    @RequestMapping("getConditionList")
    public BaseReqVo getConditionList(Integer catId,Integer sourceId,Integer yearId){
        FilmConditionVO filmConditionVO = null;
        try {
            filmConditionVO = new FilmConditionVO();
            List<CatInfoVO> catInfo = filmService.getCatInfo(catId);
            List<SourceInfoVO> sourceInfoVOS = filmService.getsourceInfo(sourceId);
            List<YearInfoVO> yearInfoVOS = filmService.getyearInfo(yearId);
            filmConditionVO.setCatInfo(catInfo);
            filmConditionVO.setSourceInfo(sourceInfoVOS);
            filmConditionVO.setYearInfo(yearInfoVOS);
        } catch (Exception e) {
            log.info("影片首页异常",e);
            return BaseReqVo.fail("影片接口异常");
        }
        return BaseReqVo.ok(filmConditionVO);
    }

    @RequestMapping(value = "getFilms")
    public BaseReqVo getFilms(FilmRequestVO filmRequestVO){
        BaseReqVo baseReqVo = new BaseReqVo();
        Map map = new HashMap();
        if(filmRequestVO.getShowType()!=null && filmRequestVO.getShowType()==1){
            map = filmService.getHotFilms(filmRequestVO);

        }else if(filmRequestVO.getShowType()!=null && filmRequestVO.getShowType()==2){
            map = filmService.getSoonFilms(filmRequestVO);

        }else if(filmRequestVO.getShowType()!=null && filmRequestVO.getShowType()==3){
            map = filmService.getClassicFilms(filmRequestVO);

        }else if(filmRequestVO.getKw()!=null){
            map = filmService.selectByKw(filmRequestVO.getKw());
        }
        if(map.get("totalPage")!=null && (int)map.get("totalPage")==0){
            baseReqVo.setStatus(1);
            baseReqVo.setMsg("查询失败，无banner可加载");
            return baseReqVo;
        }
        if(map!=null&&map.size()!=0){
            baseReqVo.setStatus(0);
            baseReqVo.setImgPre("http://img.meetingshop.cn/");
            baseReqVo.setNowPage(filmRequestVO.getNowPage());
            if(map.get("totalPage")!=null) {
                baseReqVo.setTotalPage((int) map.get("totalPage"));
            }
            baseReqVo.setData(map.get("data"));
            return baseReqVo;
        }else {
            baseReqVo.setStatus(999);
            baseReqVo.setMsg("系统出现异常，请联系管理员");
            return baseReqVo;
        }
    }

    @RequestMapping(value = "films/{UUID}")
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
