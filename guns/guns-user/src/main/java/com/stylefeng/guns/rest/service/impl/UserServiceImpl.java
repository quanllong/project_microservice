package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeActorTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeActorT;
import com.stylefeng.guns.rest.service.UserService;
import com.stylefeng.guns.rest.service.vo.UserVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Service(interfaceClass = UserService.class)
public class UserServiceImpl implements UserService {

    @Autowired
    MtimeActorTMapper mtimeActorTMapper;


    @Override
    public UserVo selectById(Integer id) {
        MtimeActorT mtimeActorT = mtimeActorTMapper.selectById(id);
        UserVo userVo = new UserVo();
        userVo.setUuid(mtimeActorT.getUuid());
        userVo.setActorName(mtimeActorT.getActorName());
        return userVo;
    }
}
