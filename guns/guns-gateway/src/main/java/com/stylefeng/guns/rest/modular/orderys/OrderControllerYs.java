package com.stylefeng.guns.rest.modular.orderys;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.OrderServiceYs;
import com.stylefeng.guns.rest.service.vo.orderys.OrderVOYs;
import com.stylefeng.guns.rest.service.vo.orderys.UserVOYs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:ys
 * @Date:2019/11/30
 * @time:15:14
 */
@RestController
@RequestMapping("order")
public class OrderControllerYs {

    @Reference(interfaceClass = OrderServiceYs.class,check = false)
    OrderServiceYs orderServiceYs;
    @Autowired
    RedisTemplate redisTemplate;

    /**
     * @param field
     * @param soldSeats
     * @param seatsName
     * @param request
     * @return
     */
    @RequestMapping(value = "buyTickets",method = RequestMethod.POST)
    public BaseReqVo buyTickets(Integer field, String soldSeats, String seatsName, HttpServletRequest request){
        String authorization = null;
        try {
            authorization = request.getHeader("Authorization");
        } catch (Exception e) {
//            e.printStackTrace();
            return BaseReqVo.fail("请检查登录信息");
        }
        //去除掉前六个字符，前六个字符是Basic加一个空格，后面的字符就是Base64编码之后的字符串.
        // 然后对该字符串进行解码，解码之后进行判断用户名和密码是否正确，如果正确的话则返回一个认证成功。
        String token = authorization.substring(7);
        //这里还没有进行熊git里面poll，没有别人的代码，关于登录的用户信息。，先随便导入一个User
        UserVOYs userVO = (UserVOYs) redisTemplate.opsForValue().get(token);
        Integer UserId = userVO.getId();


        boolean trueSeats = orderServiceYs.isTrueSeats(field + "", soldSeats);

        boolean notSoldSeats = orderServiceYs.isNotSoldSeats(field + "", soldSeats);

        if (trueSeats || notSoldSeats){
            return BaseReqVo.fail("您选择的座位已售出，请重新选择座位！");
        }
        OrderVOYs orderVOYs = orderServiceYs.saveOrderInfo(field,soldSeats,seatsName,UserId);
        return BaseReqVo.ok(orderVOYs);
    }


    @RequestMapping("getOrderInfo")
    public BaseReqVo getOrderInfo(Integer getOrderInfo,Integer pageSize){
        return null;
    }
}
