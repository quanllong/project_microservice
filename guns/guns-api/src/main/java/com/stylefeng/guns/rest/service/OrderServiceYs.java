package com.stylefeng.guns.rest.service;

import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.rest.service.vo.orderys.OrderVOYs;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author:ys
 * @Date:2019/11/30
 * @time:15:30
 */
public interface OrderServiceYs {

    //验证座位是否为真
    boolean isTrueSeats(String fieldId,String seats);

    //已经销售的座位里，有没有这些座位
    boolean isNotSoldSeats(String fieldId,String seats);

    //创建订单信息
    OrderVOYs saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId);

    //使用当前登录人获取已经购买的订单
    Page<OrderVOYs> getOrderByUserId(Integer userId);

    //根据Field获取所有已经销售的座位订单
    String getSoldSeatsByFieldId(Integer fieldId, HttpServletRequest request);
}
