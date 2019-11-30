package com.stylefeng.guns.rest.modular.user;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.service.MtimeUserTService;
import com.stylefeng.guns.rest.service.vo.MtimeUserT;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.RegisterReqVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
public class MtimeUserTServiceImpl implements MtimeUserTService {

    @Autowired
    MtimeUserTMapper mtimeUserTMapper;


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

    @Override
    public boolean login(String userName, String password) {

        List<MtimeUserVO> list = selectUserByName(userName);
        if (list != null && list.size() > 0) {
            MtimeUserVO mtimeUserVO = list.get(0);
            if (mtimeUserVO.getUserPwd().equals(password)) {
                return true;
            }
        }
        return false;
    }
}
