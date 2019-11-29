package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MtimeFieldT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 放映场次表 Mapper 接口
 * </p>
 *
 * @author quanllong
 * @since 2019-11-28
 */
public interface MtimeFieldTMapper extends BaseMapper<MtimeFieldT> {
    // quanllong
    @Select("SELECT DISTINCT film_id FROM `mtime_field_t` where cinema_id = #{cinemaId}")
    List<String> selectFilmIdDistinctByCinemaId(@Param("cinemaId") Integer cinemaId);
}
