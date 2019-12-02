package com.stylefeng.guns.rest.common.persistence.dao;

import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.service.vo.filmvo.FilmInfoVO;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * 订单信息表 Mapper 接口
 * </p>
 *
 * @author 
 * @since 2019-11-30
 */
public interface MoocOrderTMapper extends BaseMapper<MoocOrderT> {
    String getSeatsByField(@Param("field") String field);
    //ys  影片信息  本该在影院模块里面
    FilmInfoVO getFilmInfoById(@Param("field") int fieldId);

}
