package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author quanllong
 * @since 2019-12-04
 */
public interface MtimePromoStockMapper extends BaseMapper<MtimePromoStock> {

    int updateStock(@Param( value = "promoId") String promoId, @Param("amount") int amount);

    @Select("select stock from mtime_promo_stock where promo_Id = #{promoId} ")
    MtimePromoStock queryStockByPromoId(@Param("promoId") String promoId);

    List<MtimePromoStock> selectStockByCinemaIdAndPromoId(@Param("cinemaId") String cinemaId,
                                                          @Param("promoId") String promoId);
}
