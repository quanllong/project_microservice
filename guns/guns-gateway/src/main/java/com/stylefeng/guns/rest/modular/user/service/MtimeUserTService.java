package com.stylefeng.guns.rest.modular.user.service;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import com.stylefeng.guns.rest.service.vo.RegisterReqVo;

import java.util.List;

public interface MtimeUserTService {

    List<MtimeUserT> selectUserByName(String username);

    Integer insertUser(RegisterReqVo registerReqVo);

    boolean login(String userName, String password);
}
