package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.vo.OrderTestVO;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("order")
public class OrderController {

    @Reference(interfaceClass = OrderService.class,check = false)
    OrderService orderService;

    @Autowired
    RedisTemplate redisTemplate;

    @RequestMapping("query")
    public BaseReqVo query(Integer id){
        // 这个方法是用来测试项目能否跑通，跟本项目没有关系，quanlong
        OrderTestVO orderVO = orderService.queryById(id);
        return BaseReqVo.ok(orderVO);
    }

    /*
    Request URL: http://115.29.141.32/order/buyTickets?fieldId=7&soldSeats=4&seatsName=%E5%8D%95%E6%8E%92%E5%BA%A7
    Request Method: POST
     */
    @RequestMapping("buyTickets")
    public BaseReqVo buyTicket(String fieldId,String[] soldSeats,String seatsName){
        Boolean trueSeats = orderService.isTrueSeats(fieldId, soldSeats);
        if(!trueSeats){
            return BaseReqVo.fail("座位不存在");
        }
        Boolean soldSeats1 = orderService.isSoldSeats(fieldId, soldSeats);
        if(soldSeats1){
            return BaseReqVo.fail("座位已经售出");    // 没有显示具体哪个座位被售出
        }

        int userId = 1; // 暂时写成固定值，等token验证写成之后，这里要用RedisTemplate取出。
        // redisTemplate.opsForValue().get() 怎么取出用户信息，这是个问题
        OrderVO orderVO = orderService.saveOrderInfo(fieldId, soldSeats, seatsName, userId);
        if (orderVO != null ){
            return BaseReqVo.ok(orderVO);
        }
        return BaseReqVo.fail("系统出现异常，请联系管理员");
    }
}
