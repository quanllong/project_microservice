package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.MtimeUserT;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.RegisterReqVo;
import com.stylefeng.guns.rest.service.vo.UserModifyVo;

import javax.servlet.http.HttpServletRequest;
import java.util.List;


public interface MtimeUserTService {

    List<MtimeUserVO> selectUserByName(String username);

    Integer insertUser(RegisterReqVo registerReqVo);

    MtimeUserT getMtimeUserT(HttpServletRequest request);

    UserModifyVo.UserData userMessModify(MtimeUserT mtimeUser);
}
