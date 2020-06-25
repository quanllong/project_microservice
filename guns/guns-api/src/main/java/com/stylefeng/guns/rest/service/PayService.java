package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.payvo.PayInfo;

public interface PayService {
    PayInfo getQRCodeAddress(String orderId);

    int checkPayStatus(String orderId, Integer userId);

    int updateOrderStatus(String orderId, Integer userId, int status);


}
