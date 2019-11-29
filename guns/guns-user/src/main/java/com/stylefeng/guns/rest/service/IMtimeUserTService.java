package com.stylefeng.guns.rest.service;


import com.baomidou.mybatisplus.service.IService;
import com.stylefeng.guns.rest.common.persistence.beanvo.RegisterReqVo;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import java.util.List;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author wuqiangqiang
 * @since 2019-11-28
 */
public interface IMtimeUserTService extends IService<MtimeUserT> {

    List<MtimeUserT> selectUserByName(String username);

    Integer insertUser(RegisterReqVo registerReqVo);
}
