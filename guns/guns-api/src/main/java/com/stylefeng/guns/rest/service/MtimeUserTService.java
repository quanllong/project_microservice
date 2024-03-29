package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.GetUserInfoVo;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.RegisterReqVo;
import java.util.List;

public interface MtimeUserTService {

    List<MtimeUserVO> selectUserByName(String username);

    Integer insertUser(RegisterReqVo registerReqVo);

    boolean login(String userName, String password);
    
    GetUserInfoVo userMessModify(GetUserInfoVo userInfoVo);

    List<MtimeUserVO> selectUserAndPwd(String userName, String password);
}
