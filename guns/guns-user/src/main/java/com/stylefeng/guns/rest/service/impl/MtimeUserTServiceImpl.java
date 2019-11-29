package com.stylefeng.guns.rest.service.impl;


import com.stylefeng.guns.rest.common.persistence.beanvo.RegisterReqVo;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import com.stylefeng.guns.rest.service.IMtimeUserTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wuqiangqiang
 * @since 2019-11-28
 */
@Service
public class MtimeUserTServiceImpl extends ServiceImpl<MtimeUserTMapper, MtimeUserT> implements IMtimeUserTService {
    @Autowired
    MtimeUserTMapper mtimeUserTMapper;


    @Override
    public List<MtimeUserT> selectUserByName(String username) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_name", username);
        List<MtimeUserT> list = mtimeUserTMapper.selectByMap(map);
        return list;
    }




    @Override
    public Integer insertUser(RegisterReqVo registerReqVo) {
        MtimeUserT userT = new MtimeUserT();
        //userT.setUuid();
        userT.setUserName(registerReqVo.getUsername());
        userT.setUserPwd(registerReqVo.getPassword());
        userT.setNickName("阿里郎");
        //userT.setUserSex();
        //userT.setBirthday();
        userT.setEmail(registerReqVo.getEmail());
        userT.setUserPhone(registerReqVo.getMobile());
        userT.setAddress(registerReqVo.getAddress());
        //userT.setHeadUrl();
        userT.setBiography("等你来填个人介绍哦");
        //userT.setLifeState();
        userT.setBeginTime(new Date());
        userT.setUpdateTime(new Date());
        Integer insert = mtimeUserTMapper.insert(userT);
        return insert;
    }
}
