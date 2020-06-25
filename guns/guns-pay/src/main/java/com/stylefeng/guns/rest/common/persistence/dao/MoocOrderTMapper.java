package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author wuqiagnqiang
 * @since 2019-12-03
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {

    @Update("update mooc_order_t set order_status = #{status} where UUID = #{orderId}")
    int updateStatusByUuid(String orderId, int status);
}
