package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.CheckUtlis;
import com.stylefeng.guns.rest.service.MtimeUserTService;
import com.stylefeng.guns.rest.service.vo.*;
import org.apache.catalina.servlet4preview.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author wuqiangqiang
 * @since 2019-11-28
 */


@RestController
@RequestMapping("user")
public class MtimeUserTController {

    @Reference(interfaceClass = MtimeUserTService.class,check = false)
    private MtimeUserTService mtimeUserTService;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("register")
    public BaseResponseVO register(RegisterReqVo registerReqVo) throws ServletException, IOException {

        /*数据校验*/
        String username = registerReqVo.getUsername();
        Boolean flag = CheckUtlis.checkStr(username);
        if (flag == false) {
            return BaseResponseVO.fail("用户名不能出现空格");
        }
        Boolean f = CheckUtlis.checkAddress(registerReqVo.getAddress());
        if (f == false) {
            return BaseResponseVO.fail("地址必须为中文");
        }


        /*查询是否存在该用户*/
        int status = check(username).getStatus();
        if (status == 0) {
            /*插入用户数据*/
            Integer insert = mtimeUserTService.insertUser(registerReqVo);
            if (insert == 1) {
                return BaseResponseVO.ok("注册成功");
            }
            return BaseResponseVO.systemError("系统出现异常，请联系管理员");
        }
        return  BaseResponseVO.fail("用户已存在");
    }


    /**
     * 用户名验证接口:查询是否存在该用户
     *
     * @param username
     * @return
     */
    @RequestMapping("check")
    public BaseResponseVO check(String username) {
        /*判断用户名是否存在*/
        List<MtimeUserVO> list = mtimeUserTService.selectUserByName(username);

        if (list != null && list.size() > 0) {/*判断集合是否空*/
            return BaseResponseVO.fail("用户已经注册");
        }
        if (list != null && list.size() == 0) {
            return BaseResponseVO.ok("用户名不存在");
        }
        return BaseResponseVO.systemError("系统出现异常，请联系管理员");
    }


    @RequestMapping("logout")
    public BaseResponseVO logout(HttpServletRequest request) {
       /* ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();*/
        String token = request.getHeader("Authorization");
        Boolean delete = redisTemplate.delete(token);
        if (delete) {
            return BaseResponseVO.ok("成功退出");
        }else {
            return BaseResponseVO.fail("退出失败，用户尚未登陆");
        }
    }

    //莫智权:
    // 用户信息查询
    @RequestMapping("getUserInfo")
    public UserResponseVo userSearch(javax.servlet.http.HttpServletRequest request){
        UserResponseVo userSearchVo = new UserResponseVo();
        MtimeUserT mtimeUser = mtimeUserTService.getMtimeUserT(request);

        userSearchVo.setStatus(0);
        userSearchVo.setData(mtimeUser);

        return userSearchVo;
    }
//    莫智权:
//     用户信息修改
    @RequestMapping("updateUserInfo")
    public UserModifyVo userModify(MtimeUserT mtimeUser){
        UserModifyVo userModifyVo = new UserModifyVo();
        UserModifyVo.UserData userData = mtimeUserTService.userMessModify(mtimeUser);
        userModifyVo.setStatus(0);
        userModifyVo.setData(userData);
        return userModifyVo;
    }
}


