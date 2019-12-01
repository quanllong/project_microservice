package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.service.vo.MtimeUserT;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.service.MtimeUserTService;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.RegisterReqVo;
import com.stylefeng.guns.rest.service.vo.UserModifyVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author wuqiangqiang
 * @since 2019-11-28
 */
@Component
@Service(interfaceClass = MtimeUserTService.class)
public class MtimeUserTServiceImpl  implements MtimeUserTService {

    @Autowired
    MtimeUserTMapper mtimeUserTMapper;
    @Autowired
    RedisTemplate redisTemplate;
    
    @Autowired
    JwtProperties jwtProperties;

    @Override
    public List<MtimeUserVO> selectUserByName(String username) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_name", username);
        List<MtimeUserT> list = mtimeUserTMapper.selectByMap(map);
        List<MtimeUserVO> userVOList = new ArrayList<>();

        for (MtimeUserT userT : list) {
            MtimeUserVO mtimeUserVO = new MtimeUserVO();
            BeanUtils.copyProperties(userT,mtimeUserVO);
            userVOList.add(mtimeUserVO);
        }
        return userVOList;
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

    //莫智权:
    // 用户信息查询
    @Override
    public MtimeUserT getMtimeUserT(HttpServletRequest request) {
        MtimeUserT mtimeUserVo = new MtimeUserT();
        
        final String requestHeader = request.getHeader(jwtProperties.getHeader());
        String token = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer "))
            token = requestHeader.substring(7);

//        String token = request.getHeader("Authorization");
        MtimeUserT user = (MtimeUserT) redisTemplate.opsForValue().get(token);

        if(user==null){
            //抛出异常
            return null;
        }
        mtimeUserVo.setUuid(user.getUuid());
        mtimeUserVo.setUserName(user.getUserName());
        mtimeUserVo.setUserPwd(user.getUserPwd());
        mtimeUserVo.setNickName(user.getNickName());
        mtimeUserVo.setUserSex(user.getUserSex());
        mtimeUserVo.setBirthday(user.getBirthday());
        mtimeUserVo.setEmail(user.getEmail());
        mtimeUserVo.setUserPhone(user.getUserPhone());
        mtimeUserVo.setAddress(user.getAddress());
        mtimeUserVo.setHeadUrl(user.getHeadUrl());
        mtimeUserVo.setBiography(user.getBiography());
        mtimeUserVo.setLifeState(user.getLifeState());
        mtimeUserVo.setBeginTime(user.getBeginTime());
        mtimeUserVo.setUpdateTime(user.getUpdateTime());

        return mtimeUserVo;
    }

    @Override
    public UserModifyVo.UserData userMessModify(MtimeUserT mtimeUser) {
        mtimeUser.setUpdateTime(new Date());
        mtimeUserTMapper.updateById(mtimeUser);

        UserModifyVo.UserData userData = new UserModifyVo.UserData();
        Integer uuid = mtimeUser.getUuid();
        MtimeUserT userT = mtimeUserTMapper.selectById(uuid);
        userData.setId(userT.getUuid());
        userData.setUsername(userT.getUserName());
        userData.setNickname(userT.getNickName());
        userData.setEmail(userT.getEmail());
        userData.setPhone(userT.getEmail());
        userData.setSex(userT.getUserSex());
        userData.setBirthday(userT.getBirthday());
        userData.setLifeState(userT.getLifeState());
        userData.setBiography(userT.getBiography());
        userData.setAddress(userT.getAddress());
        userData.setHeadAddress(userT.getHeadUrl());
        userData.setBeginTime(userT.getBeginTime());
        userData.setUpdateTime(userT.getUpdateTime());
        
        return userData;
    }
}
