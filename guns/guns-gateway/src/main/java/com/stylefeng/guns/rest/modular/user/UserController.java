package com.stylefeng.guns.rest.modular.user;


import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.service.UserService;
import com.stylefeng.guns.rest.service.vo.UserVo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Reference(interfaceClass = UserService.class,check = false)
    UserService userService;

    @RequestMapping("query/user")
    public UserVo query(Integer id){
        UserVo userVo = userService.selectById(id);
        return userVo;
    }

}
