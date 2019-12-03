package com.stylefeng.guns.rest.modular.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.PayService;
import com.stylefeng.guns.rest.service.vo.payvo.PayInfo;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("order")
public class PayController {
    @Reference(interfaceClass = PayService.class,check = false)
    PayService payService;

    /*
    Request URL: http://localhost/order/getPayInfo?orderId=f2314e500a5547419cf3
    Request Method: POST
    请求体没有发送东西
     */
    @RequestMapping("getPayInfo")
    public BaseReqVo getPayInfo(String orderId){
        PayInfo payInfo = payService.getQRCodeAddress(orderId);
        if(payInfo != null){
            HashMap<String, Object> map = new HashMap<>();
            map.put("orderId",orderId);
            map.put("qRCodeAddress",payInfo.getQRCodeAddress());
            BaseReqVo<Object> reqVo = new BaseReqVo<>();
            reqVo.setImgPre(payInfo.getImgPre());
            reqVo.setData(map);
            reqVo.setStatus(0);
            return reqVo;
        }
        BaseReqVo<String> reqVo = new BaseReqVo<>();
        reqVo.setStatus(1);
        reqVo.setMsg("订单支付失败，请稍后重试q_Q");
        return reqVo;
    }


}
