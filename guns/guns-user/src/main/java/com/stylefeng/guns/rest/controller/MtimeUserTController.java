package com.stylefeng.guns.rest.controller;

import com.stylefeng.guns.rest.common.persistence.beanvo.BaseResponseVO;
import com.stylefeng.guns.rest.common.persistence.beanvo.RegisterReqVo;
import com.stylefeng.guns.rest.common.persistence.model.B;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import com.stylefeng.guns.rest.service.IMtimeUserTService;
import com.stylefeng.guns.rest.utils.CheckUtlis;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import redis.clients.jedis.Jedis;
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

    @Autowired
    IMtimeUserTService mtimeUserTService;

    /*@Autowired
    Jedis jedis;*/

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("register")
    public BaseResponseVO register(RegisterReqVo registerReqVo) throws ServletException, IOException {
       /* B b = new B();
        b.setAge(13);
        b.setName("aa");
        redisTemplate.opsForValue().set("b", b);
        B b1 = (B) redisTemplate.opsForValue().get("b");*/


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
        List<MtimeUserT> list = mtimeUserTService.selectUserByName(username);

        if (list != null && list.size() > 0) {/*判断集合是否空*/
            return BaseResponseVO.fail("用户已经注册");
        }
        if (list != null && list.size() == 0) {
            return BaseResponseVO.ok("用户名不存在");
        }
        return BaseResponseVO.systemError("系统出现异常，请联系管理员");
    }
}


