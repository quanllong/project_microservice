package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.service.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author:ys
 * @Date:2019/11/28
 * @time:21:42
 */
@Component
@Service(interfaceClass = FilmService.class)
public class FilmServiceImpl implements FilmService {

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
    public List<BannersVO> getBanners() {
        EntityWrapper<MtimeBannerT> objectEntityWrapper = new EntityWrapper<>();
        objectEntityWrapper.eq("is_valid",1);
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
}
