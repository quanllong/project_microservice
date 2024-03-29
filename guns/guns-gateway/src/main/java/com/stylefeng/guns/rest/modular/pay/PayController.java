package com.stylefeng.guns.rest.modular.pay;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.consistant.OrderStatus;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.PayService;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.payvo.PayInfo;
import com.stylefeng.guns.rest.service.vo.payvo.PayResultVO;

import com.stylefeng.guns.rest.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

@RestController
@RequestMapping("order")
public class PayController {
    @Reference(interfaceClass = PayService.class,check = false)
    PayService payService;

    @Autowired
    TokenUtils tokenUtils;
    @Autowired
    RedisTemplate redisTemplate;

    @Reference(interfaceClass = OrderService.class,check = false)
    OrderService orderService;

    /*
    Request URL: http://localhost/order/getPayInfo?orderId=f2314e500a5547419cf3
    Request Method: POST
    请求体没有发送东西
     */
    @RequestMapping("getPayInfo")
    public BaseReqVo getPayInfo(String orderId){

        // 判断是否已经生成过二维码（幂等性）
        if(redisTemplate.hasKey(orderId)) {
            BaseReqVo<Object> reqVo = (BaseReqVo<Object>) redisTemplate.opsForValue().get(orderId);
            reqVo.setStatus(0);
            reqVo.setMsg("二维码已生成，请扫码支付");
            return reqVo;
        }
        // 避免重复生成二维码
        String status = (String) redisTemplate.opsForHash().get("orderId--status", orderId);
        if(status != null ){
            if("ok".equals(status)){
                BaseReqVo<Object> reqVo1 = new BaseReqVo<>();
                reqVo1.setMsg("二维码已生成，请使用支付宝扫码支付");
                reqVo1.setStatus(1);
                return reqVo1;
            }
        }

        PayInfo payInfo = payService.getQRCodeAddress(orderId);

        if(payInfo != null){
            HashMap<String, Object> map = new HashMap<>();
            map.put("orderId",orderId);
            map.put("qRCodeAddress",payInfo.getQRCodeAddress());
            BaseReqVo<Object> reqVo2 = new BaseReqVo<>();
            reqVo2.setImgPre(payInfo.getImgPre());
            reqVo2.setData(map);
            reqVo2.setStatus(0);
            // 存进缓存
            redisTemplate.opsForValue().set(orderId,reqVo2);
            // 把状态存入redis,1表示已经生成过二维码
            redisTemplate.opsForHash().put("orderId--status",orderId,"ok");
            return reqVo2;
        }
        BaseReqVo<String> reqVo3 = new BaseReqVo<>();
        reqVo3.setStatus(1);
        reqVo3.setMsg("订单支付失败，请稍后重试q_Q");
        return reqVo3;
    }

    /*
    Request URL: http://localhost/order/getPayResult?orderId=92e399b2305d48908920&tryNums=1
    Request Method: POST
     */
    @RequestMapping("getPayResult")
    public PayResultVO getPayResult(@RequestParam(required = true,name = "orderId") String orderId,
                                    @RequestParam(required = true,name = "tryNums") String tryNums,
                                    HttpServletRequest request)
        {

        PayResultVO payResultVO = new PayResultVO();
        HashMap<String, Object> data = new HashMap<>();

        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if(mtimeUserVO == null){
            payResultVO.setMsg("用户未登录，查询失败");
            return payResultVO;
        }
        Integer userId = mtimeUserVO.getUuid();

        System.out.println("当前查询次数：" + tryNums);

        // 查询订单的支付状态
        int status = payService.checkPayStatus(orderId, userId);

        if(OrderStatus.PAY_SUCCESS.getCode() == status){
            // 修改数据库，修改成功返回1，否则0
            int update = payService.updateOrderStatus(orderId,userId,status);
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
        } else if (OrderStatus.CLOSED.getCode() == status){
            payResultVO.setStatus(1);
            payResultVO.setMsg("订单超时，已关闭");
        } else if(OrderStatus.NOT_PAY.getCode() == status){
            payResultVO.setMsg("订单未支付");
            payResultVO.setStatus(999);
        }
        return payResultVO;
    }

}
