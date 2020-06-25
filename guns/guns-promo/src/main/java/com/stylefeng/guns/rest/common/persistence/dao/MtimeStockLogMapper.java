package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author quanllong
 * @since 2019-12-05
 */
public interface MtimeStockLogMapper extends BaseMapper<MtimeStockLog> {

    @Update("update mtime_stock_log set status = #{status} where uuid = #{stockLogId} ")
    int updateStatusById(@Param("stockLogId") String stockLogId, @Param("status") int status);
}
