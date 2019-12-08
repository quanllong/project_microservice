package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeHallDictT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 地域信息表 Mapper 接口
 * </p>
 *
 * @author quanllong
 * @since 2019-11-30
 */
public interface MtimeHallDictTMapper extends BaseMapper<MtimeHallDictT> {

    MtimeHallDictT queryHallDictByFieldId(@Param("fieldId") Integer fieldId);
}
