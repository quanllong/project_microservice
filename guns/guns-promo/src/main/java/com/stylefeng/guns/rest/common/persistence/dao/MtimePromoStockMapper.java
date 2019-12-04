package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author quanllong
 * @since 2019-12-04
 */
public interface MtimePromoStockMapper extends BaseMapper<MtimePromoStock> {

    @Update("update mtime_promo_stock set stock = stock - #{stock} where promo_id = #{promoId} ")
    int updateStock(@Param("promoId") Integer promoId, @Param("stock") int stock);
}
