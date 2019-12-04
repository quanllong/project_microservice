package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.CheckUtlis;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.service.MtimeUserTService;
import com.stylefeng.guns.rest.service.vo.*;
import com.stylefeng.guns.rest.service.vo.BaseResponseVO;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.RegisterReqVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
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

    @Reference(interfaceClass = MtimeUserTService.class, check = false)
    private MtimeUserTService mtimeUserTService;

    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    JwtProperties jwtProperties;

    @Autowired
    MtimeUserTMapper mtimeUserTMapper;

    @RequestMapping("register")
    public BaseResponseVO register(RegisterReqVo registerReqVo) {
        try {
            //制造异常
            //int i = 1/0;<<

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
            }
            return BaseResponseVO.fail("该用户已经注册");
        } catch (Exception e) {
            return BaseResponseVO.systemError("系统出现异常，请联系管理员");
        }
    }
    /**
     * 用户名验证接口:查询是否存在该用户
     *
     * @param username
     * @return
     */
    @RequestMapping("check")
    public BaseResponseVO check(String username) {

        try {
            /*判断用户名是否存在*/
            List<MtimeUserVO> list = mtimeUserTService.selectUserByName(username);

            if (list != null && list.size() > 0) {/*判断集合是否空*/
                return BaseResponseVO.fail("用户已经注册");
            }
            if (list != null && list.size() == 0) {
                return BaseResponseVO.ok("用户名不存在");
            }
        }catch (Exception e){
            return BaseResponseVO.systemError("系统出现异常，请联系管理员");
        }
        return null;
    }

/*@Autowired
HttpServletRequest request2;*/

    @RequestMapping("logout")
    public BaseResponseVO logout(HttpServletRequest request) {

       /* ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();*/
       try{
           String authorization = request.getHeader("Authorization");

           if(authorization == null) {
               return BaseResponseVO.fail("前端未传入Authorization");
           }
           String token = authorization.substring(7);
           Object o = redisTemplate.opsForValue().get(token);
           if (o == null) {
               /*token失效，让其直接退出*/
               return BaseResponseVO.ok("成功退出");
           }

           /*删除 redis中的token*/
           Boolean delete = redisTemplate.delete(token);
           if (delete) {
               return BaseResponseVO.ok("成功退出");
           }
       }catch (Exception e){
           return BaseResponseVO.systemError("系统出现异常，请联系管理员");
       }
        return null;
    }

    //莫智权:
    // 用户信息展示
    @RequestMapping("getUserInfo")
    public UserResponseVo userSearch(HttpServletRequest request){
        UserResponseVo userSearchVo = new UserResponseVo();

        String token = getToken(request);
        if(token==null){
            return UserResponseVo.fail("查询失败，用户尚未登陆");
        }

        //通过token获得uuid
        MtimeUserVO user = (MtimeUserVO) redisTemplate.opsForValue().get(token);
        if(user==null){
            return UserResponseVo.fail("查询失败，用户尚未登陆");
        }
        int uuid = user.getUuid();

        //通过uuid从数据库获得对象信息
        GetUserInfoVo getUserInfoVo = mtimeUserTMapper.getMtimeUserByUuid(uuid);

        userSearchVo.setStatus(0);
        userSearchVo.setData(getUserInfoVo);

        return userSearchVo;
    }
//     莫智权:
//     用户信息修改
    @RequestMapping("updateUserInfo")
    public UserResponseVo userModify(GetUserInfoVo mtimeUser) {
        UserResponseVo userModifyVo = new UserResponseVo();
        GetUserInfoVo user = mtimeUserTService.userMessModify(mtimeUser);
        userModifyVo.setStatus(0);
        userModifyVo.setData(user);
        return userModifyVo;
    }

    //获取token
    public String getToken(HttpServletRequest request){
        final String requestHeader = request.getHeader(jwtProperties.getHeader());
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer "))
            token = requestHeader.substring(7);

        return token;
    }
    public BaseResponseVO logout() {
        /*String method = request2.getMethod();
        String authorization1 = request2.getHeader("Authorization");*/
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        try {
            //制造异常
            //int i = 1/0;
            String authorization = request.getHeader("Authorization");
            if (authorization == null) {
                // return BaseResponseVO.fail("前端未传入Authorization");
                return BaseResponseVO.ok("成功退出");   // quanllong改的。token已经过期，但是页面右上角还显示着用户，这时候点击退出的话，要允许它直接退出
            }
            if (authorization != null && !authorization.startsWith("Bearer ")) {
                // return BaseResponseVO.fail("该Authorization无效");
                return BaseResponseVO.ok("成功退出");   // quanllong改的
            }

            String token = authorization.substring(7);
            MtimeUserVO o = (MtimeUserVO) redisTemplate.opsForValue().get(token);
            /*String userName = o.getUserName();*/
            if (o == null) {
                return BaseResponseVO.fail("退出失败，用户尚未登陆");
            }

            /*删除 redis中的token*/
            Boolean delete = redisTemplate.delete(token);
            if (delete) {
                return BaseResponseVO.ok("成功退出");
            }
        } catch (Exception e) {
            return BaseResponseVO.systemError("系统出现异常，请联系管理员");
        }
        return null;
    }
}


