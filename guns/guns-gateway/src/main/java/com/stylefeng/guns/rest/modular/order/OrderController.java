package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.vo.OrderVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Reference(interfaceClass = OrderService.class)
    OrderService orderService;

    @RequestMapping("query")
    public BaseReqVo query(Integer id){
        // 这个方法是用来测试项目能否跑通，跟本项目没有关系，quanlong
        OrderVO orderVO = orderService.queryById(id);
        return BaseReqVo.ok(orderVO);
    }
}
