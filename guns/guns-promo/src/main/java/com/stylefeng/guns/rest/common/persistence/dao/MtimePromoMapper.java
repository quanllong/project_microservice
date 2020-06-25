package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.service.vo.promovo.PromoData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author quanllong
 * @since 2019-12-04
 */
public interface MtimePromoMapper extends BaseMapper<MtimePromo> {

    List<PromoData> queryPromoDataByCinemaId(@Param("cinemaId") Integer cameraId,
                                             @Param("promoId") String promoId);
}
