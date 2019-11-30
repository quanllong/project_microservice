package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.github.pagehelper.IPage;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.service.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Service(interfaceClass = FilmService.class)
public class FilmServiceImpl implements FilmService {

    @Autowired
    MtimeFilmTMapper mtimeFilmTMapper;
    @Autowired
    MtimeFilmInfoTMapper mtimeFilmInfoTMapper;
    @Autowired
    MtimeActorTMapper mtimeActorTMapper;
    @Autowired
    MtimeFilmActorTMapper mtimeFilmActorTMapper;
    @Autowired
    MtimeCatDictTMapper mtimeCatDictTMapper;
    @Autowired
    MtimeSourceDictTMapper mtimeSourceDictTMapper;


    @Override
    public FilmVO selectById(Integer id) {
        MtimeFilmT mtimeFilmT = mtimeFilmTMapper.selectById(id);
        FilmVO filmVO = new FilmVO();
        filmVO.setFilmName(mtimeFilmT.getFilmName());
        filmVO.setUuid(mtimeFilmT.getUUID());
        return filmVO;
    }

    @Override
    public Map getHotFilms(FilmRequestVO filmRequestVO) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("film_status","1");
        if(filmRequestVO.getYearId()!=99) {
            wrapper.eq("film_date", filmRequestVO.getYearId());
        }
        if(filmRequestVO.getSourceId()!=99) {
            wrapper.eq("film_area", filmRequestVO.getSourceId());
        }
        if(filmRequestVO.getCatId()!=99) {
            wrapper.eq("film_cats", filmRequestVO.getCatId());
        }
        if(filmRequestVO.getSortId()==1) {
            wrapper.orderBy("film_box_office", false);
        }else if(filmRequestVO.getSortId()==2){
            wrapper.orderBy("film_time", false);
        }else if(filmRequestVO.getSortId()==3){
            wrapper.orderBy("film_score", false);
        }
        Page page = new Page<>(filmRequestVO.getNowPage(),filmRequestVO.getPageSize());
        List<MtimeFilmT> list = mtimeFilmTMapper.selectPage(page, wrapper);

        List<FilmDetailVO> filmDetailVOS = new ArrayList<>();
        for (MtimeFilmT o : list) {
            FilmDetailVO filmDetailVO = new FilmDetailVO();
            filmDetailVO.setFileType(o.getFilmType());
            filmDetailVO.setFilmId(o.getUUID());
            filmDetailVO.setImgAddress(o.getImgAddress());
            filmDetailVO.setFilmName(o.getFilmName());
            filmDetailVO.setFilmScore(o.getFilmScore());
            filmDetailVOS.add(filmDetailVO);
        }
        Integer nums = list.size();
        int remainder = nums%filmRequestVO.getPageSize();
        int totalPage;
        if(nums==0){
            totalPage = 1;
        }else {
            if(remainder==0) {
                totalPage = nums / filmRequestVO.getPageSize();
            }else {
                totalPage = nums /filmRequestVO.getPageSize() + 1;
            }
        }
        Map map = new HashMap();
        map.put("totalPage",totalPage);
        map.put("data",filmDetailVOS);
        return map;
    }

    @Override
    public Map getSoonFilms(FilmRequestVO filmRequestVO) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("film_status","2");
        if(filmRequestVO.getYearId()!=99) {
            wrapper.eq("film_date", filmRequestVO.getYearId());
        }
        if(filmRequestVO.getSourceId()!=99) {
            wrapper.eq("film_area", filmRequestVO.getSourceId());
        }
        if(filmRequestVO.getCatId()!=99) {
            wrapper.eq("film_cats", filmRequestVO.getCatId());
        }
        if(filmRequestVO.getSortId()==1) {
            wrapper.orderBy("film_box_office", false);
        }else if(filmRequestVO.getSortId()==2){
            wrapper.orderBy("film_time", false);
        }else if(filmRequestVO.getSortId()==3){
            wrapper.orderBy("film_score", false);
        }
        Page page = new Page<>(filmRequestVO.getNowPage(),filmRequestVO.getPageSize());

        List<MtimeFilmT> list = mtimeFilmTMapper.selectPage(page, wrapper);

        List<FilmDetailVO> filmDetailVOS = new ArrayList<>();
        for (MtimeFilmT o : list) {
            FilmDetailVO filmDetailVO = new FilmDetailVO();
            filmDetailVO.setFileType(o.getFilmType());
            filmDetailVO.setFilmId(o.getUUID());
            filmDetailVO.setImgAddress(o.getImgAddress());
            filmDetailVO.setFilmName(o.getFilmName());
            filmDetailVO.setFilmScore(o.getFilmScore());
            filmDetailVOS.add(filmDetailVO);
        }
        Integer nums = list.size();
        int remainder = nums%filmRequestVO.getPageSize();
        int totalPage;
        if(nums==0){
            totalPage = 1;
        }else {
            if(remainder==0) {
                totalPage = nums / filmRequestVO.getPageSize();
            }else {
                totalPage = nums /filmRequestVO.getPageSize() + 1;
            }
        }
        Map map = new HashMap();
        map.put("totalPage",totalPage);
        map.put("data",filmDetailVOS);
        return map;
    }

    @Override
    public Map getClassicFilms(FilmRequestVO filmRequestVO) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("film_status","3");
        if(filmRequestVO.getYearId()!=99) {
            wrapper.eq("film_date", filmRequestVO.getYearId());
        }
        if(filmRequestVO.getSourceId()!=99) {
            wrapper.eq("film_area", filmRequestVO.getSourceId());
        }
        if(filmRequestVO.getCatId()!=99) {
            wrapper.eq("film_cats", filmRequestVO.getCatId());
        }
        if(filmRequestVO.getSortId()==1) {
            wrapper.orderBy("film_box_office", false);
        }else if(filmRequestVO.getSortId()==2){
            wrapper.orderBy("film_time", false);
        }else if(filmRequestVO.getSortId()==3){
            wrapper.orderBy("film_score", false);
        }
        Page page = new Page<>(filmRequestVO.getNowPage(),filmRequestVO.getPageSize());
        List<MtimeFilmT> list = mtimeFilmTMapper.selectPage(page, wrapper);

        List<FilmDetailVO> filmDetailVOS = new ArrayList<>();
        for (MtimeFilmT o : list) {
            FilmDetailVO filmDetailVO = new FilmDetailVO();
            filmDetailVO.setFileType(o.getFilmType());
            filmDetailVO.setFilmId(o.getUUID());
            filmDetailVO.setImgAddress(o.getImgAddress());
            filmDetailVO.setFilmName(o.getFilmName());
            filmDetailVO.setFilmScore(o.getFilmScore());
            filmDetailVOS.add(filmDetailVO);
        }
        Integer nums = list.size();
        int remainder = nums%filmRequestVO.getPageSize();
        int totalPage;
        if(nums==0){
            totalPage = 1;
        }else {
            if(remainder==0) {
                totalPage = nums / filmRequestVO.getPageSize();
            }else {
                totalPage = nums /filmRequestVO.getPageSize() + 1;
            }
        }
        Map map = new HashMap();
        map.put("totalPage",totalPage);
        map.put("data",filmDetailVOS);
        return map;
    }

    @Override
    public Map films(Integer UUID) {
        Map data = new HashMap();
        MtimeFilmInfoT mtimeFilmInfoT = mtimeFilmInfoTMapper.selectById(UUID);
        data.put("filmEnName",mtimeFilmInfoT.getFilmEnName());
        data.put("filmId",mtimeFilmInfoT.getFilmId());

        MtimeFilmT mtimeFilmT = mtimeFilmTMapper.selectById(UUID);
        data.put("filmName",mtimeFilmT.getFilmName());
        data.put("imgAddress",mtimeFilmT.getImgAddress());

        String film_cats = mtimeFilmT.getFilmCats();
        String[] split = film_cats.split("#");
        String info01 = "";
        for (String s : split) {
            if(!s.equals("")&&s!=null){
                MtimeCatDictT mtimeCatDictT = mtimeCatDictTMapper.selectById(s);
                if(info01==""){
                    info01 += mtimeCatDictT.getShowName();
                }else {
                    info01 = info01 + "," + mtimeCatDictT.getShowName();
                }
            }
        }
        data.put("info01",info01);

        String info02 = "";
        Integer film_source = mtimeFilmT.getFilmSource();
        MtimeSourceDictT mtimeSourceDictT = mtimeSourceDictTMapper.selectById(film_source);
        info02 += mtimeSourceDictT.getShowName();
        info02 = info02 + " / " + mtimeFilmInfoT.getFilmLength();
        data.put("info02",info02);

        String info03 = mtimeFilmT.getFilmTime() + mtimeSourceDictT.getShowName() + "上映";
        data.put("info03",info03);


        //演员
        EntityWrapper entityWrapper = new EntityWrapper();
        entityWrapper.eq("film_id",mtimeFilmInfoT.getFilmId());
        List<MtimeFilmActorT> film_actor = mtimeFilmActorTMapper.selectList(entityWrapper);
        //响应体内部actors
        List actors_a = new ArrayList();
        for (int i = 0; i < film_actor.size(); i++) {
            MtimeActorT mtimeActorT = mtimeActorTMapper.selectById(film_actor.get(i).getActorId());
            ActorVO temp = new ActorVO();
            temp.setDirectorName(mtimeActorT.getActorName());
            temp.setImgAddress(mtimeActorT.getActorImg());
            temp.setRoleName("演员"+(i+1));
            actors_a.add(temp);
        }

        //导演
        MtimeActorT mtimeActorT = mtimeActorTMapper.selectById(mtimeFilmInfoT.getDirectorId());
        ActorVO director = new ActorVO();
        director.setDirectorName(mtimeActorT.getActorName());
        director.setImgAddress(mtimeActorT.getActorImg());
        Map actors = new HashMap();
        actors.put("actors",actors_a);
        actors.put("director",director);

        Map info04 = new HashMap();
        info04.put("actors",actors);
        info04.put("biopgraphy",mtimeFilmInfoT.getBiography());
        info04.put("filmId",mtimeFilmInfoT.getFilmId());
        Map imgVO = new HashMap();
        String[] split1 = mtimeFilmInfoT.getFilmImgs().split(",");
        imgVO.put("mainImg",split1[0]);
        for(int i = 1;i < split1.length; i++){
            if(i<10) {
                imgVO.put("img0" + i, split1[i]);
            }else {
                imgVO.put("img" + i, split1[i]);
            }
        }
        info04.put("imgVO",imgVO);
        data.put("info04",info04);

        data.put("score",mtimeFilmInfoT.getFilmScore());
        data.put("scoreNum",mtimeFilmInfoT.getFilmScoreNum());
        data.put("totalBox",mtimeFilmT.getFilmBoxOffice());
        return data;
    }

    @Override
    public Map selectByKw(String kw) {
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.like("fileName", kw).or().like("film_en_name",kw);
        Page page = new Page(1,18);
        List list = mtimeFilmInfoTMapper.selectMapsPage(page, wrapper);

        int nums = list.size();
        int remainder = nums%18;
        int totalPage;
        if(nums==0){
            totalPage = 1;
        }else {
            if(remainder==0) {
                totalPage = nums / 18;
            }else {
                totalPage = nums / 18 + 1;
            }
        }
        Map map = new HashMap();
        map.put("totalPage",totalPage);
        map.put("data",list);
        return map;
    }
}
