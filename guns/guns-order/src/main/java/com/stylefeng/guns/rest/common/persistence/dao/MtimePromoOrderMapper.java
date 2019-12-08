package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderVO;
import com.stylefeng.guns.rest.service.vo.promovo.PromoData;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author quanllong
 * @since 2019-12-07
 */
public interface MtimePromoOrderMapper extends BaseMapper<MtimePromoOrder> {

    List<OrderVO> selectPromoOrderByUserId(@Param("userId") int userId);
}
