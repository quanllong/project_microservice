package com.stylefeng.guns.rest.service;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.stylefeng.guns.core.util.UUIDUtils;
import com.stylefeng.guns.rest.common.persistence.dao.MoocOrderTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeCinemaTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFieldTMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeFilmTMapper;
import com.stylefeng.guns.rest.common.persistence.model.MoocOrderT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeCinemaT;
import com.stylefeng.guns.rest.common.persistence.model.MtimeFieldT;
import com.stylefeng.guns.rest.service.vo.filmvo.FilmInfoVO;
import com.stylefeng.guns.rest.service.vo.orderys.OrderVOYs;
import com.stylefeng.guns.rest.service.vo.orderys.UserVOYs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author:ys
 * @Date:2019/11/30
 * @time:16:04
 */
@Component
@Service(interfaceClass = OrderServiceYs.class)
public class OrderServiceImpl implements OrderServiceYs {

    @Autowired
    MoocOrderTMapper orderTMapper;
    @Autowired
    MtimeFilmTMapper mtimeFilmTMapper;
    @Autowired
    MtimeFieldTMapper fieldTMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MtimeCinemaTMapper cinemaTMapper;

    /**
     * 判断售出的票是否是真
     * @param fieldId
     * @param seats
     * @return
     */
    @Override
    public boolean isTrueSeats(String fieldId, String seats) {
        String seatsByField = orderTMapper.getSeatsByField(fieldId);
        //根据seatsField去寻找json路径，然后根据根据该路径将json进行转化成字符串
        //"ids":"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24"
        //假装转了,先注释 等会打开
        /*String s = seatsByField.get("ids").toString;
        String[] querySeats = s.split(",");
        String[] seat = seats.split(",");
        int size = 0;
        for (String querySeat : querySeats) {
            for (String s1 : seat) {
                if (s1.equals(querySeat)){
                    size ++;
                }
            }
        }
        //判断顾客所选的座位是否真实有
        if (seat.length != size){
            return false;
        }*/
        return true;
    }

    /**
     * 已经售出的座位里，是否有这些座位
     * @param fieldId
     * @param seats
     * @return
     */
    @Override
    public boolean isNotSoldSeats(String fieldId, String seats) {
        String[] split = seats.split(",");
        EntityWrapper<MoocOrderT> objectEntityWrapper = new EntityWrapper<>();
        objectEntityWrapper.eq("filed_id",fieldId);
        List<MoocOrderT> moocOrderTS = orderTMapper.selectList(objectEntityWrapper);
        for (MoocOrderT moocOrderT : moocOrderTS) {
            String[] split1 = moocOrderT.getSeatsIds().split(",");
            for (String s : split1) {
                for (String s1 : split) {
                    if (s1.equalsIgnoreCase(s)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * 创建订单信息
     * @param fieldId
     * @param soldSeats
     * @param seatsName
     * @param userId
     * @return
     */
    @Override
    public OrderVOYs saveOrderInfo(Integer fieldId, String soldSeats, String seatsName, Integer userId) {
        //获取随机的一个订单号
        String uuid = UUIDUtils.getUuid();
        FilmInfoVO filmInfo = orderTMapper.getFilmInfoById(fieldId);
        String filmName = filmInfo.getFilmName();
        MtimeFieldT mtimeFieldT = fieldTMapper.selectById(fieldId);
        Integer cinemaId = mtimeFieldT.getCinemaId();
        MtimeCinemaT mtimeCinemaT = cinemaTMapper.selectById(cinemaId);
        String cinemaName = mtimeCinemaT.getCinemaName();
        Integer price = mtimeFieldT.getPrice();
        Double filmPrice = Double.parseDouble(price+"");
        //求该客户的订单
        int solds = soldSeats.split(",").length;
        //总金额
        double totalPrice = filmPrice * solds;
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setUuid(uuid);
        moocOrderT.setSeatsIds(soldSeats);
        moocOrderT.setSeatsName(seatsName);
        moocOrderT.setOrderUser(userId);
        moocOrderT.setFilmPrice(filmPrice);
        moocOrderT.setOrderPrice(totalPrice);
        moocOrderT.setFieldId(fieldId);
        moocOrderT.setCinemaId(cinemaId);
        moocOrderT.setOrderTime(new Date());
        //将订单信息插入订单表中，生成一个订单
        Integer insert = orderTMapper.insert(moocOrderT);
        if (insert > 0){
            MoocOrderT moocOrderT1 = orderTMapper.selectById(uuid);
            OrderVOYs orderVOYs = new OrderVOYs();
            if (moocOrderT1 == null || moocOrderT1.getUuid() == null){
                return null;
            }else {
                orderVOYs.setOrderId(moocOrderT1.getUuid());
                orderVOYs.setFilmName(filmName);
                Date date = moocOrderT1.getOrderTime();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH;mm:ss");
                String orderTime = simpleDateFormat.format(date);
                orderVOYs.setOrderTimestamp(orderTime);
                orderVOYs.setSeatsName(moocOrderT1.getSeatsName());
                orderVOYs.setOrderPrice(moocOrderT1.getOrderPrice() + "");
                orderVOYs.setFieldTime(orderTime);
                orderVOYs.setCinemaName(cinemaName);
                return orderVOYs;
            }
        }else {
            return null;
        }
    }

    /**
     * 使用当前登录人获取已经购买的订单
     * @param userId
     * @return
     */
    @Override
    public Page<OrderVOYs> getOrderByUserId(Integer userId) {
        Page<OrderVOYs> page = new Page<>();
        if (userId == null){
            return null;
        }else {
            EntityWrapper wrapper = new EntityWrapper();
            wrapper.eq("order_user",userId);
            List<OrderVOYs> list = orderTMapper.selectList(wrapper);
            if (CollectionUtils.isEmpty(list)){
                page.setTotal(0);
                page.setRecords(new ArrayList<>());
                return page;
            }else {
                EntityWrapper wrapper1 = new EntityWrapper();
                wrapper.eq("order_user",userId);
                Integer integer = orderTMapper.selectCount(wrapper1);
                page.setTotal(integer);
                page.setRecords(list);
                return page;
            }
        }
    }

    /**
     * 根据FieldId获取所有已经销售的座位编号
     * @param fieldId
     * @return
     */
    @Override
    public String getSoldSeatsByFieldId(Integer fieldId, HttpServletRequest request) {
        String authorization = null;
        try {
            authorization = request.getHeader("Authorization");
        } catch (Exception e) {
//            e.printStackTrace();
            return "";
        }
        //去除掉前六个字符，前六个字符是Basic加一个空格，后面的字符就是Base64编码之后的字符串.
        // 然后对该字符串进行解码，解码之后进行判断用户名和密码是否正确，如果正确的话则返回一个认证成功。
        String token = authorization.substring(7);
        //这里还没有进行熊git里面poll，没有别人的代码，关于登录的用户信息。，先随便导入一个User
        UserVOYs userVO = (UserVOYs) redisTemplate.opsForValue().get(token);
        Integer orderUser = userVO.getId();
        if (fieldId == null){
            return "";
        }else {
            MoocOrderT moocOrderT = new MoocOrderT();
            moocOrderT.setOrderUser(orderUser);
            moocOrderT.setFieldId(fieldId);
            MoocOrderT moocOrderT1 = orderTMapper.selectOne(moocOrderT);
            String seatsIds = moocOrderT1.getSeatsIds();
            return seatsIds;
        }
    }
}
