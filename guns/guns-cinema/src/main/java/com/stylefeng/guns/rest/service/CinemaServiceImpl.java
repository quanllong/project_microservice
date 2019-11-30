package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.service.vo.cinemavo.*;
import com.stylefeng.guns.rest.service.vo.CinemaVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;

@Component
@Service(interfaceClass = CinemaService.class)
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    MtimeCinemaTMapper mtimeCinemaTMapper;
    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;
    @Autowired
    MtimeHallFilmInfoTMapper mtimeHallFilmInfoTMapper;

    /**
     * @param id
     * @return
     * 这是用来测试项目有没有跑通的，与本项目关系不大
     */
    @Override
    public CinemaVO selectById(Integer id) {
        MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.selectById(id);
        CinemaVO cinemaVO = new CinemaVO();
        cinemaVO.setCinemaName(mtimeCinemaT.getCinemaName());
        cinemaVO.setUuid(mtimeCinemaT.getUuid());
        return cinemaVO;
    }

    //接口1
    @Override
    public List<CinemaVO> getCinemas(CinemaQueryVo cinemaQueryVo) {
        ArrayList<CinemaVO> cinemaVOArrayList=new ArrayList<>();

        Integer brandId = cinemaQueryVo.getBrandId();
        Integer areaId = cinemaQueryVo.getAreaId();
        Integer hallType = cinemaQueryVo.getHallType();
        String s = hallType.toString();

        String hall_ids="#"+s+"#";
        EntityWrapper wrapper=new EntityWrapper();

        if (brandId!=99){
            wrapper.eq("brand_id",brandId);
        }
        if (areaId!=99){
            wrapper.eq("area_id",areaId);
        }
        if (hallType!=99){
            wrapper.like("hall_ids",hall_ids);
        }

        List<MtimeCinemaT> mtimeCinemaTList = mtimeCinemaTMapper.selectList(wrapper);
        for (MtimeCinemaT mtimeCinemaT : mtimeCinemaTList) {
            CinemaVO cinemaVO=new CinemaVO();
            cinemaVO.setCinemaName(mtimeCinemaT.getCinemaName());
            cinemaVO.setCinemaAddress(mtimeCinemaT.getCinemaAddress());
            cinemaVO.setMinimumPrice(mtimeCinemaT.getMinimumPrice());
            cinemaVO.setUuid(mtimeCinemaT.getUuid());
            cinemaVOArrayList.add(cinemaVO);
        }

        return cinemaVOArrayList;



    }



    //接口2
    @Autowired
    MtimeBrandDictTMapper mtimeBrandDictTMapper;
    @Override
    public List<BrandVo> selectBrandByBrandId(Integer brandId) {

        EntityWrapper wrapper = new EntityWrapper();
        List<BrandVo> brandVoList = new ArrayList<>();

        List<MtimeBrandDictT> mtimeBrandDictTList = mtimeBrandDictTMapper.selectList(wrapper);
        for (MtimeBrandDictT mtimeBrandDictT : mtimeBrandDictTList) {

            BrandVo brandVo = new BrandVo();
            brandVo.setBrandId(mtimeBrandDictT.getUuid());
            brandVo.setBrandName(mtimeBrandDictT.getShowName());
            if (mtimeBrandDictT.getUuid()==brandId){
                brandVo.setActive(true);
            }
            brandVoList.add(brandVo);
        }


        return brandVoList;


    }




    @Autowired
    MtimeAreaDictTMapper mtimeAreaDictTMapper;
    @Override
    public List<AreaVo> selectAreaByBrandId(Integer areaId) {
        List<AreaVo> areaVoList = new ArrayList<>();
        EntityWrapper wrapper = new EntityWrapper();

        List<MtimeAreaDictT> mtimeAreaDictTList = mtimeAreaDictTMapper.selectList(wrapper);
        for (MtimeAreaDictT mtimeAreaDictT : mtimeAreaDictTList) {

            AreaVo areaVo = new AreaVo();

            areaVo.setAreaId(mtimeAreaDictT.getUuid());
            areaVo.setAreaName(mtimeAreaDictT.getShowName());
            if (mtimeAreaDictT.getUuid()==areaId){
                areaVo.setActive(true);
            }
            areaVoList.add(areaVo);
        }



        return areaVoList;
    }

    @Autowired
    MtimeHallDictTMapper mtimeHallDictTMapper;
    @Override
    public List<HallTypeVo> selectHallByBrandType(Integer hallType) {
        List<HallTypeVo> hallTypeVoList=new ArrayList<>();
        EntityWrapper wrapper = new EntityWrapper();
        List<MtimeHallDictT> mtimeHallDictTList = mtimeHallDictTMapper.selectList(wrapper);

        for (MtimeHallDictT mtimeHallDictT : mtimeHallDictTList) {
            HallTypeVo hallTypeVo = new HallTypeVo();
            hallTypeVo.setHalltypeId(mtimeHallDictT.getUuid());
            hallTypeVo.setHalltypeName(mtimeHallDictT.getShowName());
            if (mtimeHallDictT.getUuid()==hallType){
                hallTypeVo.setActive(true);
            }
            hallTypeVoList.add(hallTypeVo);
        }



        return hallTypeVoList;

    }


    /**
     * @param cinemaId
     * @return 根据影院id返回该影院信息
     */
    @Override
    public CinemaInfoVO getCinemaInfoById(Integer cinemaId) {
        MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.selectById(cinemaId);
        CinemaInfoVO cinemaInfoVO = new CinemaInfoVO();
        // BeanUtils.copyProperties(mtimeCinemaT,cinemaInfoVO);
        cinemaInfoVO.setCinemaId(mtimeCinemaT.getUuid());
        cinemaInfoVO.setCinemaName(mtimeCinemaT.getCinemaName());
        cinemaInfoVO.setCinemaAdress(mtimeCinemaT.getCinemaAddress());
        cinemaInfoVO.setCinemaPhone(mtimeCinemaT.getCinemaPhone());
        cinemaInfoVO.setImgUrl(mtimeCinemaT.getImgAddress());
        return cinemaInfoVO;
    }

    /**
     * @param cinemaId
     * @return 根据cinemaId取得该影院上映的电影信息
     */
    @Override
    public List<FilmInfoVO> getFilmInfoByCinemaId(Integer cinemaId) {
        // 根据影院id查出电影的id，必须去重复。在column前面加上distinct或者group by film_id
        List<String> filmIds = mtimeFieldTMapper.selectFilmIdDistinctByCinemaId(cinemaId);
        ArrayList<FilmInfoVO> fieldVOArrayList = new ArrayList<>();
        for (String filmId : filmIds) {
            MtimeHallFilmInfoT hallFilmInfoT = new MtimeHallFilmInfoT();
            hallFilmInfoT.setFilmId(Integer.valueOf(filmId));
            MtimeHallFilmInfoT hallFilmInfoT1 = mtimeHallFilmInfoTMapper.selectOne(hallFilmInfoT);

            FilmInfoVO filmInfoVO = new FilmInfoVO();
            filmInfoVO.setFilmId(filmId);
            filmInfoVO.setFilmName(hallFilmInfoT1.getFilmName());
            filmInfoVO.setFilmLength(hallFilmInfoT1.getFilmLength());
            filmInfoVO.setFilmType(hallFilmInfoT1.getFilmLanguage());
            filmInfoVO.setFilmCats(hallFilmInfoT1.getFilmCats());
            filmInfoVO.setActors(hallFilmInfoT1.getActors());
            filmInfoVO.setImgAddress(hallFilmInfoT1.getImgAddress());

            // 根据电影id查询该电影的场次信息
            List<FilmFieldVO> filmFields = getFilmfieldsByFilmId(cinemaId,filmId);
            filmInfoVO.setFileFields(filmFields);

            fieldVOArrayList.add(filmInfoVO);
        }
        return fieldVOArrayList;
    }

    // 根据和影院id以及电影id查询该影院中某部电影的场次信息,并封装成一个list
    private List<FilmFieldVO> getFilmfieldsByFilmId(Integer cinemaId,String filmId){
        EntityWrapper<MtimeFieldT> mtimeFieldTEntityWrapper = new EntityWrapper<>();
        HashMap<String, Object> params = new HashMap<>();
        params.put("cinema_id",cinemaId);
        params.put("film_id",filmId);
        mtimeFieldTEntityWrapper.allEq(params);
        List<MtimeFieldT> mtimeFieldTS = mtimeFieldTMapper.selectList(mtimeFieldTEntityWrapper);
        ArrayList<FilmFieldVO> filmFieldVOArrayList = new ArrayList<FilmFieldVO>();
        for (MtimeFieldT mtimeFieldT : mtimeFieldTS) {
            FilmFieldVO filmFieldVO = new FilmFieldVO();
            filmFieldVO.setBeginTime(mtimeFieldT.getBeginTime());
            filmFieldVO.setEndTime(mtimeFieldT.getEndTime());
            filmFieldVO.setFieldId(String.valueOf(mtimeFieldT.getHallId())); //放映厅的编号
            filmFieldVO.setHallName(mtimeFieldT.getHallName());
            // 查language
            /*EntityWrapper<MtimeHallFilmInfoT> hallFilmInfoTEntityWrapper = new EntityWrapper<>();
            hallFilmInfoTEntityWrapper.eq("film_id",mtimeFieldT.getFilmId());*/
            MtimeHallFilmInfoT hallFilmInfoT = new MtimeHallFilmInfoT();
            hallFilmInfoT.setFilmId(mtimeFieldT.getFilmId());
            MtimeHallFilmInfoT hallFilmInfoT1 = mtimeHallFilmInfoTMapper.selectOne(hallFilmInfoT);
            filmFieldVO.setLanguage(hallFilmInfoT1.getFilmLanguage());
            filmFieldVO.setPrice(String.valueOf(mtimeFieldT.getPrice()));
            filmFieldVOArrayList.add(filmFieldVO);
        }
        return filmFieldVOArrayList;
    }

}
