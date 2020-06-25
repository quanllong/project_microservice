package com.stylefeng.guns.rest.modular.order;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.consistant.OrderStatus;
import com.stylefeng.guns.rest.consistant.RedisPrefixConsistant;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.OrderTestVO;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderPayStatus;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderVO;
import com.stylefeng.guns.rest.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("order")
public class OrderController {

    @Reference(interfaceClass = OrderService.class,check = false)
    OrderService orderService;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    TokenUtils tokenUtils;

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
    public BaseReqVo buyTicket(String fieldId, String[] soldSeats, String seatsName, HttpServletRequest request){
        // 验证有没有登录
        // 使用工具类
        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if(mtimeUserVO == null){
            return BaseReqVo.fail("请先登录");
        }

        // 检查当前用户的待支付订单
        Integer userId = mtimeUserVO.getUuid();
        String key = String.format(RedisPrefixConsistant.CURRENT_ORDER,userId);
        OrderPayStatus orderPayStatus = (OrderPayStatus) redisTemplate.opsForValue().get(key);
        if(orderPayStatus != null ){
            if (OrderStatus.NOT_PAY.getCode().equals(orderPayStatus.getStatus())){
                return BaseReqVo.fail("您还有未支付的订单,请支付后再下单");
            }
        }

        // 座位不存在，则放回false
        Boolean trueSeats = orderService.isTrueSeats(fieldId, soldSeats);
        if(!trueSeats){
            return BaseReqVo.fail("座位不存在");
        }

        // 座位已经售出，则返回true
        Boolean soldSeats1 = orderService.isSoldSeats(fieldId, soldSeats);
        if(soldSeats1){
            return BaseReqVo.fail("座位已经售出");    // 没有显示具体哪个座位被售出
        }


        // int userId = 1; // 暂时写成固定值，等token验证写成之后，这里要用RedisTemplate取出。
        OrderVO orderVO = orderService.saveOrderInfo(fieldId, soldSeats, seatsName, userId);
        if (orderVO != null ){
            return BaseReqVo.ok(orderVO);
        }
        return BaseReqVo.fail("系统出现异常，请联系管理员");
    }
    /*
     /order/getOrderInfo
     查询用户订单
     */
    @RequestMapping("getOrderInfo")
    public BaseReqVo getOrderInfo(String nowPage,String pageSize,HttpServletRequest request){
        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if(mtimeUserVO == null){
            return BaseReqVo.fail("请先登录");
        }

        // int userId = 1;
        Integer userId = mtimeUserVO.getUuid();
        List<OrderVO> orders = orderService.getOrderByUserId(nowPage,pageSize,userId);
        if(orders != null && orders.size() != 0){
            Integer size = Integer.valueOf(pageSize);
            int orderSize = orders.size();
            int totalPages = orderSize % size == 0 ? (orderSize / size) : (orderSize / size + 1);
            BaseReqVo reqVo = BaseReqVo.ok(orders);// orderStatus传回来是空字符串，还要转为已关闭，已完成状态
            reqVo.setTotalPage(totalPages);
            return reqVo;
        }
        BaseReqVo fail = BaseReqVo.fail("订单列表为空哦！~");
        fail.setStatus(1);
        return fail;
    }
}
