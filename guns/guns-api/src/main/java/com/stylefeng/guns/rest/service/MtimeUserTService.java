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

<<<<<<< HEAD
    MtimeUserT getMtimeUserT(HttpServletRequest request);

    UserModifyVo.UserData userMessModify(MtimeUserT mtimeUser);
=======
    boolean login(String userName, String password);
>>>>>>> a808af76e19b74fabb19ffbf2552afe0b66a4ac5
}
