package com.stylefeng.guns.rest.modular.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.PayService;
import com.stylefeng.guns.rest.service.vo.payvo.PayInfo;
import com.stylefeng.guns.rest.service.vo.payvo.PayResultVO;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
@RequestMapping("order")
public class PayController {
    @Reference(interfaceClass = PayService.class,check = false)
    PayService payService;
    @Reference(interfaceClass = OrderService.class,check = false)
    OrderService orderService;

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

    /*
    Request URL: http://localhost/order/getPayResult?orderId=92e399b2305d48908920&tryNums=1
    Request Method: POST
     */
    @RequestMapping("getPayResult")
    public PayResultVO getPayResult(String orderId,String tryNums){
        // 查询是否支付
        boolean flag = payService.check(orderId);
        PayResultVO payResultVO = new PayResultVO();
        if(flag){
            // 修改数据库，修改成功返回1，否则0
            int status = 1; // 传入的值
            int update = orderService.updateOrderStatus(orderId,status);
            if(update == 1){
                payResultVO.setOrderId(orderId);
                payResultVO.setOrderMsg("支付成功");
                payResultVO.setOrderStatus(1);
                return payResultVO;
            } else {
                // 支付成功，但数据库没改成功
                System.out.println("订单已经支付，但是数据库没改成功");
            }
        } else {
            payResultVO.setStatus(1);
            payResultVO.setMsg("订单支付失败，请稍后重试");
        }
        payResultVO.setMsg("系统异常，请联系管理员");
        payResultVO.setStatus(999);
        return payResultVO;
    }


}
