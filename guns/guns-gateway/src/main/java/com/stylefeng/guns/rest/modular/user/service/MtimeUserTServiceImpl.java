package com.stylefeng.guns.rest.modular.user.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeUserTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import com.stylefeng.guns.rest.service.MtimeUserTService;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.RegisterReqVo;
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

    /**
     * 根据username查询
     * @param username
     * @return
     */
    @Override
    public List<MtimeUserVO> selectUserByName(String username) {
        Map<String,Object> map = new HashMap<>();
        map.put("user_name", username);
        List<MtimeUserVO> list = mtimeUserTMapper.selectByUserName(username);
        return list;

        /*List<MtimeUserVO> userVOList = new ArrayList<>();

        for (MtimeUserT userT : list) {
            MtimeUserVO mtimeUserVO = new MtimeUserVO();
            BeanUtils.copyProperties(userT,mtimeUserVO);
            userVOList.add(mtimeUserVO);
        }*/
    }

    @Override
    public boolean login(String userName, String password) {

        List<MtimeUserVO> list = mtimeUserTMapper.selectUserByNameAndPwd(userName,password);
        if (list != null && list.size() > 0) {
            MtimeUserVO mtimeUserVO = list.get(0);
            if (mtimeUserVO.getUserPwd().equals(password)) {
                return true;
            }
        }
        return false;
    }




    @Override
    public Integer insertUser(RegisterReqVo registerReqVo) {
        MtimeUserT userT = new MtimeUserT();
        //userT.setUuid();
        userT.setUserName(registerReqVo.getUsername());
        userT.setUserPwd(registerReqVo.getPassword());
        userT.setNickName("在下坂本，有何贵干");
        //userT.setUserSex();
        //userT.setBirthday();
        userT.setEmail(registerReqVo.getEmail());
        userT.setUserPhone(registerReqVo.getMobile());
        userT.setAddress(registerReqVo.getAddress());
        //userT.setHeadUrl();
        userT.setBiography("这个家伙很懒，还没有填写个人说明");
        userT.setLifeState(0);
        userT.setBeginTime(new Date());
        userT.setUpdateTime(new Date());
        Integer insert = mtimeUserTMapper.insert(userT);
        return insert;
    }
}
