package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.service.vo.filmvo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.*;

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
    @Autowired
    MtimeBannerTMapper mtimeBannerTMapper;
    @Autowired
    MtimeFilmTMapper filmTMapper;
    @Autowired
    MtimeCatDictTMapper catDictTMapper;
    @Autowired
    MtimeSourceDictTMapper sourceDictTMapper;
    @Autowired
    MtimeYearDictTMapper yearDictTMapper;

    @Override
    public FilmVO selectById(Integer id) {
        MtimeFilmT mtimeFilmT = mtimeFilmTMapper.selectById(id);
        FilmVO filmVO = new FilmVO();
        filmVO.setFilmName(mtimeFilmT.getFilmName());
        filmVO.setUuid(mtimeFilmT.getUuid());
        return filmVO;
    }

    @Override
    public List<BannersVO> getBanners() {
        EntityWrapper<MtimeBannerT> objectEntityWrapper = new EntityWrapper<>();
        objectEntityWrapper.eq("is_valid",0);
        List<MtimeBannerT> mtimeBannerTS = mtimeBannerTMapper.selectList(objectEntityWrapper);
        ArrayList<BannersVO> bannersList = new ArrayList<>();
        if (CollectionUtils.isEmpty(mtimeBannerTS)){
            return bannersList;
        }
        for (MtimeBannerT bannerT : mtimeBannerTS) {
            BannersVO bannersVo = new BannersVO();
            bannersVo.setBannerId(bannerT.getUuid() + "");
            bannersVo.setBannerAddress(bannerT.getBannerAddress());
            bannersVo.setBannerUrl(bannerT.getBannerUrl());
            bannersList.add(bannersVo);
        }
        return bannersList;
    }

    @Override
    public FilmsVO getHotFilms(boolean isLimit, int nums) {
        FilmsVO filmsVO = new FilmsVO();
        EntityWrapper<MtimeFilmT> filmTEntityWrapper = new EntityWrapper<>();
        filmTEntityWrapper.eq("film_status",1);
        Integer filmNum = filmTMapper.selectCount(filmTEntityWrapper);
        List<MtimeFilmT> mtimeFilmTS = null;
        if (isLimit){
            Page<Object> page = new Page<>(1,nums);
            mtimeFilmTS = filmTMapper.selectPage(page, filmTEntityWrapper);
        }else {
            mtimeFilmTS = filmTMapper.selectList(filmTEntityWrapper);
        }
        List<FilmInfoVO> list = convert2FilmInfoVo1(mtimeFilmTS);
        filmsVO.setFilmNum(filmNum);
        filmsVO.setFilmInfo(list);
        return filmsVO;
    }

    private List<FilmInfoVO> convert2FilmInfoVo1(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmInfoVO> filmInfoVoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(mtimeFilmTS)){
            return filmInfoVoList;
        }
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmInfoVO filmInfoVo = new FilmInfoVO();
            filmInfoVo.setFilmId(mtimeFilmT.getUuid() + "");
            filmInfoVo.setFilmType(mtimeFilmT.getFilmType());
            filmInfoVo.setImgAddress(mtimeFilmT.getImgAddress());
            filmInfoVo.setFilmName(mtimeFilmT.getFilmName());
            filmInfoVo.setFilmScore(mtimeFilmT.getFilmScore());
            filmInfoVoList.add(filmInfoVo);
        }
        return filmInfoVoList;
    }

    @Override
    public FilmsVO getSoonFilms(boolean isLimit, int nums) {
        FilmsVO filmsVO = new FilmsVO();
        EntityWrapper<MtimeFilmT> filmTEntityWrapper = new EntityWrapper<>();
        filmTEntityWrapper.eq("film_status",2);
        Integer filmNum = filmTMapper.selectCount(filmTEntityWrapper);
        List<MtimeFilmT> mtimeFilmTS = null;
        if (isLimit){
            Page<Object> page = new Page<>(1,nums);
            mtimeFilmTS = filmTMapper.selectPage(page, filmTEntityWrapper);
        }else {
            mtimeFilmTS = filmTMapper.selectList(filmTEntityWrapper);
        }
        List<FilmInfoVO> list = convert2FilmInfoVo2(mtimeFilmTS);
        filmsVO.setFilmNum(filmNum);
        filmsVO.setFilmInfo(list);
        return filmsVO;
    }

    private List<FilmInfoVO> convert2FilmInfoVo2(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmInfoVO> filmInfoVoList = new ArrayList<>();
        if (CollectionUtils.isEmpty(mtimeFilmTS)){
            return filmInfoVoList;
        }
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmInfoVO filmInfoVo = new FilmInfoVO();
            filmInfoVo.setFilmId(mtimeFilmT.getUuid() + "");
            filmInfoVo.setFilmType(mtimeFilmT.getFilmType());
            filmInfoVo.setImgAddress(mtimeFilmT.getImgAddress());
            filmInfoVo.setFilmName(mtimeFilmT.getFilmName());
            filmInfoVo.setExpectNum(mtimeFilmT.getFilmPresalenum());
            filmInfoVo.setShowTime(mtimeFilmT.getFilmTime());
            filmInfoVoList.add(filmInfoVo);
        }
        return filmInfoVoList;

    }

    @Override
    public List<FilmInfoVO> getBoxRanking(Integer count) {
        List<FilmInfoVO> filmInfoVOArrayList = new ArrayList<>();
        Page<MtimeFilmT> page = new Page<MtimeFilmT>(1,count,"film_box_office",false);
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        List<MtimeFilmT> mtimeFilmTS = filmTMapper.selectPage(page, entityWrapper);
        if (CollectionUtils.isEmpty(mtimeFilmTS)){
            return filmInfoVOArrayList;
        }
        filmInfoVOArrayList = conver2BoxRanking(mtimeFilmTS);
        return filmInfoVOArrayList;
    }

    private List<FilmInfoVO> conver2BoxRanking(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmInfoVO> list = new ArrayList<>();
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmInfoVO filmInfoVO = new FilmInfoVO();
            filmInfoVO.setFilmId(mtimeFilmT.getUuid() + "");
            filmInfoVO.setImgAddress(mtimeFilmT.getImgAddress());
            filmInfoVO.setFilmName(mtimeFilmT.getFilmName());
            filmInfoVO.setBoxNum(mtimeFilmT.getFilmBoxOffice());
            list.add(filmInfoVO);
        }
        return list;
    }

    @Override
    public List<FilmInfoVO> getExpectRanking(Integer count) {

        List<FilmInfoVO> filmInfoVOArrayList = new ArrayList<>();
        Page<MtimeFilmT> page = new Page<MtimeFilmT>(1,count,"film_preSaleNum",false);
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        List<MtimeFilmT> mtimeFilmTS = filmTMapper.selectPage(page, entityWrapper);
        if (CollectionUtils.isEmpty(mtimeFilmTS)){
            return filmInfoVOArrayList;
        }
        filmInfoVOArrayList = conver2ExpectRanking(mtimeFilmTS);
        return filmInfoVOArrayList;
    }

    private List<FilmInfoVO> conver2ExpectRanking(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmInfoVO> list = new ArrayList<>();
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmInfoVO filmInfoVO = new FilmInfoVO();
            filmInfoVO.setFilmId(mtimeFilmT.getUuid() + "");
            filmInfoVO.setImgAddress(mtimeFilmT.getImgAddress());
            filmInfoVO.setFilmName(mtimeFilmT.getFilmName());
            filmInfoVO.setExpectNum(mtimeFilmT.getFilmPresalenum());
            list.add(filmInfoVO);
        }
        return list;
    }

    @Override
    public List<FilmInfoVO> getTop100(Integer count) {
        List<FilmInfoVO> filmInfoVOArrayList = new ArrayList<>();
        Page<MtimeFilmT> page = new Page<MtimeFilmT>(1,count,"film_score",false);
        EntityWrapper<MtimeFilmT> entityWrapper = new EntityWrapper<>();
        List<MtimeFilmT> mtimeFilmTS = filmTMapper.selectPage(page, entityWrapper);
        if (CollectionUtils.isEmpty(mtimeFilmTS)){
            return filmInfoVOArrayList;
        }
        filmInfoVOArrayList = conver2Top100(mtimeFilmTS);
        return filmInfoVOArrayList;
    }

    @Override
    public List<CatInfoVO> getCatInfo(Integer catId) {
        ArrayList<CatInfoVO> catInfoList = new ArrayList<>();
        EntityWrapper<MtimeCatDictT> mtimeCatDictTEntityWrapper = new EntityWrapper<>();
        List<MtimeCatDictT> mtimeCatDictTS = catDictTMapper.selectList(mtimeCatDictTEntityWrapper);
        if (CollectionUtils.isEmpty(mtimeCatDictTS)){
            return catInfoList;
        }
        for (MtimeCatDictT mtimeCatDictT : mtimeCatDictTS) {
            CatInfoVO catInfoVO = new CatInfoVO();
            catInfoVO.setCatId(mtimeCatDictT.getUuid() + "");
            catInfoVO.setCatName(mtimeCatDictT.getShowName());
            if (mtimeCatDictT.getUuid() == catId){
                catInfoVO.setIsActive(true);
            }
            catInfoList.add(catInfoVO);
        }
        return catInfoList;
    }

    @Override
    public List<SourceInfoVO> getsourceInfo(Integer sourceId) {
        ArrayList<SourceInfoVO> sourceList = new ArrayList<>();
        EntityWrapper<MtimeSourceDictT> sourceDictTEntityWrapper = new EntityWrapper<>();
        List<MtimeSourceDictT> mtimeSourceDictTS = sourceDictTMapper.selectList(sourceDictTEntityWrapper);
        if (CollectionUtils.isEmpty(mtimeSourceDictTS)){
            return sourceList;
        }
        for (MtimeSourceDictT sourceDictTourceDictT : mtimeSourceDictTS) {
            SourceInfoVO sourceInfoVO = new SourceInfoVO();
            sourceInfoVO.setSourceId(sourceDictTourceDictT.getUuid() + "");
            sourceInfoVO.setSourceName(sourceDictTourceDictT.getShowName());
            if (sourceDictTourceDictT.getUuid() == sourceId){
                sourceInfoVO.setIsActive(true);
            }
            sourceList.add(sourceInfoVO);
        }
        return sourceList;
    }

    @Override
    public List<YearInfoVO> getyearInfo(Integer yearId) {
        List<YearInfoVO> list = new ArrayList<>();
        EntityWrapper<MtimeYearDictT> mtimeYearDictTEntityWrapper = new EntityWrapper<>();
        List<MtimeYearDictT> mtimeYearDictTS = yearDictTMapper.selectList(mtimeYearDictTEntityWrapper);
        if (CollectionUtils.isEmpty(mtimeYearDictTS)){
            return list;
        }
        for (MtimeYearDictT mtimeYearDictT : mtimeYearDictTS) {
            YearInfoVO yearInfoVO = new YearInfoVO();
            yearInfoVO.setYearId(mtimeYearDictT.getUuid() + "");
            yearInfoVO.setYearName(mtimeYearDictT.getShowName());
            if (mtimeYearDictT.getUuid() == yearId){
                yearInfoVO.setIsActive(true);
            }
            list.add(yearInfoVO);
        }
        return list;
    }

    private List<FilmInfoVO> conver2Top100(List<MtimeFilmT> mtimeFilmTS) {
        ArrayList<FilmInfoVO> list = new ArrayList<>();
        for (MtimeFilmT mtimeFilmT : mtimeFilmTS) {
            FilmInfoVO filmInfoVO = new FilmInfoVO();
            filmInfoVO.setFilmId(mtimeFilmT.getUuid() + "");
            filmInfoVO.setImgAddress(mtimeFilmT.getImgAddress());
            filmInfoVO.setFilmName(mtimeFilmT.getFilmName());
            filmInfoVO.setFilmScore(mtimeFilmT.getFilmScore());
            list.add(filmInfoVO);
        }
        return list;
    }

    @Override
    public Map getHotFilms(FilmRequestVO filmRequestVO) {
        Map map = new HashMap();
        EntityWrapper wrapper = new EntityWrapper();
        wrapper.eq("film_status","1");
        if(filmRequestVO.getYearId()!=99) {
            wrapper.eq("film_date", filmRequestVO.getYearId());
        }
        if(filmRequestVO.getSourceId()!=99) {
            wrapper.eq("film_area", filmRequestVO.getSourceId());
        }
        if(filmRequestVO.getCatId()!=99) {
            wrapper.like("film_cats", filmRequestVO.getCatId()+"");
        }
        if(filmRequestVO.getSortId()==1) {
            wrapper.orderBy("film_box_office", false);
        }else if(filmRequestVO.getSortId()==2){
            wrapper.orderBy("film_time", false);
        }else if(filmRequestVO.getSortId()==3){
            wrapper.orderBy("film_score", false);
        }
        List<MtimeFilmT> list = new ArrayList<>();
        if(filmRequestVO.getNowPage()!=null && filmRequestVO.getPageSize()!=null) {
            Page page = new Page<>(filmRequestVO.getNowPage(), filmRequestVO.getPageSize());
            list = mtimeFilmTMapper.selectPage(page, wrapper);
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
            map.put("totalPage",totalPage);
        }else {
            list = mtimeFilmTMapper.selectList(wrapper);
        }

        List<FilmDetailVO> filmDetailVOS = new ArrayList<>();
        for (MtimeFilmT o : list) {
            FilmDetailVO filmDetailVO = new FilmDetailVO();
            filmDetailVO.setFileType(o.getFilmType());
            filmDetailVO.setFilmId(o.getUuid());
            filmDetailVO.setImgAddress(o.getImgAddress());
            filmDetailVO.setFilmName(o.getFilmName());
            filmDetailVO.setFilmScore(o.getFilmScore());
            filmDetailVOS.add(filmDetailVO);
        }
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
            filmDetailVO.setFilmId(o.getUuid());
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
            filmDetailVO.setFilmId(o.getUuid());
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

        //修改时间格式
        Date filmTime = mtimeFilmT.getFilmTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = sdf.format(filmTime);
        String info03 = time + mtimeSourceDictT.getShowName() + "上映";
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
        wrapper.like("film_name",kw);
        Page page = new Page(1,18);
        List<MtimeFilmT> list = mtimeFilmTMapper.selectPage(page, wrapper);

        List<FilmDetailVO> filmDetailVOS = new ArrayList<>();
        for (MtimeFilmT o : list) {
            FilmDetailVO filmDetailVO = new FilmDetailVO();
            filmDetailVO.setFileType(o.getFilmType());
            filmDetailVO.setFilmId(o.getUuid());
            filmDetailVO.setImgAddress(o.getImgAddress());
            filmDetailVO.setFilmName(o.getFilmName());
            filmDetailVO.setFilmScore(o.getFilmScore());
            filmDetailVOS.add(filmDetailVO);
        }

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
        map.put("data",filmDetailVOS);
        return map;
    }
}
