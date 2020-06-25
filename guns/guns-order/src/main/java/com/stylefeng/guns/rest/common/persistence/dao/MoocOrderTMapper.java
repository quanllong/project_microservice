package com.stylefeng.guns.rest.common.persistence.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author quanllong
 * @since 2019-11-30
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {

    @Update("update mooc_order_t set order_status = #{status} where UUID = #{orderId}")
    int updateStatusByUuid(@Param("orderId") String orderId, @Param("status") int status);
}
