package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeAreaDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeBrandDictTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeHallDictTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeAreaDictT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeBrandDictT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeHallDictT;
import com.stylefeng.guns.rest.service.vo.cinemavo.AreaVo;
import com.stylefeng.guns.rest.service.vo.cinemavo.BrandVo;
import com.stylefeng.guns.rest.service.vo.CinemaVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.CinemaQueryVo;
import com.stylefeng.guns.rest.service.vo.cinemavo.HallTypeVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Service(interfaceClass = CinemaService.class)
public class CinemaServiceImpl implements CinemaService {

    @Autowired
    MtimeCinemaTMapper mtimeCinemaTMapper;

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

}
