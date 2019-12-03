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
    public PayResultVO getPayResult(String orderId, String tryNums){

        PayResultVO payResultVO = new PayResultVO();
        HashMap<String, Object> data = new HashMap<>();
        if(Integer.valueOf(tryNums) > 13){
            payResultVO.setMsg("订单超时未支付");
            return payResultVO;
        }
        System.out.println("当前查询次数：" + tryNums);

        // 查询是否支付
        boolean flag = payService.check(orderId);
        if(flag){
            // 修改数据库，修改成功返回1，否则0
            int status = 1; // 传入的值
            int update = orderService.updateOrderStatus(orderId,status);
            if(update == 1){
                data.put("orderStatus",1);
                data.put("orderMsg","支付成功");
                payResultVO.setData(data);
                return payResultVO;
            } else {
                // 支付成功，但数据库没改成功
                data.put("orderStatus",0);
                data.put("orderMsg","订单已经支付，但是数据库没更新成功");
                System.out.println("订单已经支付，但是数据库没更新成功");
            }
        } else {
            payResultVO.setStatus(1);
            payResultVO.setMsg("订单支付失败，请稍后重试");
        }
        payResultVO.setMsg("订单未支付或者被关闭");
        payResultVO.setStatus(999);
        return payResultVO;
    }


}
