package com.stylefeng.guns.rest.common.persistence.dao;


import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 * @author wuqiangqiang
 * @since 2019-11-28
 */
public interface MtimeUserTMapper extends BaseMapper<MtimeUserT> {

    List<MtimeUserVO> selectByUserName(@Param("name") String username);

    List<MtimeUserVO> selectUserByNameAndPwd(@Param("username") String userName, @Param("pwd") String password);
}
