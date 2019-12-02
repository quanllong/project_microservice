package com.stylefeng.guns.rest.modular.film;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.FilmService;
import com.stylefeng.guns.rest.service.vo.filmvo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}
