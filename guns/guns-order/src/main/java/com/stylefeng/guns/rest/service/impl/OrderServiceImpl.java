package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Service(interfaceClass = OrderService.class)
public class OrderServiceImpl implements OrderService {

    @Autowired
    MoocOrderTMapper moocOrderTMapper;

    /**
     * @param id
     * @return
     * 这是用来测试项目有没有跑通的，与本项目关系不大
     */
    @Override
    public OrderVO queryById(Integer id) {

        List<MoocOrderT> moocOrderTList = moocOrderTMapper.selectList(new EntityWrapper<MoocOrderT>().eq("cinema_id", id));
        // MoocOrderT moocOrderT = moocOrderTMapper.selectByCinemaId(id);
        MoocOrderT moocOrderT = moocOrderTList.get(0);
        OrderVO orderVO = new OrderVO();
        orderVO.setUuid(moocOrderT.getUuid());
        orderVO.setSeatsName(moocOrderT.getSeatsName());
        return orderVO;
    }
}
