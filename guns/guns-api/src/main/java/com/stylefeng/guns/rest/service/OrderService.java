package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.OrderTestVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.HallInfoVO;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderVO;
import java.util.List;

public interface OrderService {
    OrderTestVO queryById(Integer id);

    // 方法接口1，quanllong
    Boolean isTrueSeats (String fieldId,String[] seatId);

    // 方法接口2,quanllong
    Boolean isSoldSeats (String fieldId,String[] seatId);

    // 方法接口3，quanllong
    OrderVO saveOrderInfo(String fieldId,String[] seatId,String seatsName,Integer userId);

    List<OrderVO> getOrderByUserId(String nowPage, String pageSize, int userId);

    // 供影院模块的接口4的方法2使用
    HallInfoVO getFilmFieldInfo(Integer cameraId,Integer fieldId);
}
