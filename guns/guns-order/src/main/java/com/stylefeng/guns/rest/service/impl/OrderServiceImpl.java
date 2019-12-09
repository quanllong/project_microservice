package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.google.gson.Gson;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.consistant.OrderStatus;
import com.stylefeng.guns.rest.consistant.RedisPrefixConsistant;
import com.stylefeng.guns.rest.mq.Producer;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.bena.Seats;
import com.stylefeng.guns.rest.service.vo.OrderTestVO;
import com.stylefeng.guns.rest.service.vo.cinemavo.HallInfoVO;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderPayStatus;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import java.util.*;

@Component
@Service(interfaceClass = OrderService.class)
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    MoocOrderTMapper moocOrderTMapper;
    @Autowired
    MtimeFieldTMapper mtimeFieldTMapper;
    @Autowired
    MtimeHallDictTMapper mtimeHallDictTMapper;
    @Autowired
    MtimeFilmTMapper mtimeFilmTMapper;
    @Autowired
    MtimeCinemaTMapper mtimeCinemaTMapper;
    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MtimePromoOrderMapper mtimePromoOrderMapper;
    @Autowired
    Gson gson;
    @Autowired
    Producer producer;

    /**
     * 这是用来测试项目有没有跑通的，与本项目关系不大
     * @param id
     * @return
     */
    @Override
    public OrderTestVO queryById(Integer id) {

        List<MoocOrderT> moocOrderTList = moocOrderTMapper.selectList(new EntityWrapper<MoocOrderT>().eq("cinema_id", id));
        // MoocOrderT moocOrderT = moocOrderTMapper.selectByCinemaId(id);
        MoocOrderT moocOrderT = moocOrderTList.get(0);
        OrderTestVO orderVO = new OrderTestVO();
        orderVO.setUuid(moocOrderT.getUuid());
        orderVO.setSeatsName(moocOrderT.getSeatsName());
        return orderVO;
    }

    @Override
    public Boolean isTrueSeats(String fieldId, String[] seatId) {
        Seats seats = seatsFromJsonFile(Integer.valueOf(fieldId));    // seats/123214.json
        if (seats == null){
            return false;
        }
        // 用户可能选中多个座位，只要有一个座位不是当前影厅的座位，就返回false
        String ids = seats.getIds();
        List<String> list = Arrays.asList(ids.split(","));
        for (String id: seatId){
            if(! list.contains(id)){
                return false;
            }
        }
        return true;
    }

    /**
     * 读json文件，获得这个影厅的座位表
     * @param fieldId
     * @return 返回座位表对象
     */
    private Seats seatsFromJsonFile(Integer fieldId){

        // 联合查询
        MtimeHallDictT mtimeHallDictT = mtimeHallDictTMapper.queryHallDictByFieldId(fieldId);

        // 如果是全部，则默认是4dx厅
        String seatAddress = mtimeHallDictT.getSeatAddress();
        if (seatAddress == null){
            seatAddress = "seats/4dx.json";
        }

        // 尝试取出座位表对象，如果取出的对象为空，则说明reids还没有
        Seats seatsInRedis = (Seats) redisTemplate.opsForValue().get(seatAddress);
        if(seatsInRedis != null){
            return seatsInRedis;
        }

        // 读座位表
        // InputStream stream = this.getClass().getClassLoader().getResourceAsStream("seats.123214.json");
        ClassPathResource classPathResource = new ClassPathResource(seatAddress);
        String jsonStr = null;
        try {
            InputStream inputStream = classPathResource.getInputStream();
            ByteArrayOutputStream byteArrayStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            int length = 0;
            while( (length = inputStream.read(bytes)) != -1 ){
                byteArrayStream.write(bytes,0,length);
            }
            jsonStr = byteArrayStream.toString("utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(jsonStr == null){
            return null;
        }
        // 转成对象
        Seats seats = gson.fromJson(jsonStr, Seats.class);
        // 存入座位表对象
        redisTemplate.opsForValue().set(seatAddress,seats);
        return seats;
    }

    /**
     * 判断座位是否售出
     * @param fieldId 放映场次id
     * @param seatId 座位id
     * @return 座位如果被售出，返回true
     */
    @Override
    public Boolean isSoldSeats(String fieldId, String[] seatId) {
        // 调用方法获取售出座位
        String soldSeats = getSoldSeatsByFieldId(fieldId);
        // 将它转为列表，方便判断
        List<String> list = Arrays.asList(soldSeats.split(","));
        // 判断座位是否已售出
        for (String s : seatId) {
            if(list.contains(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * 获取已经售出的座位编号
     * 供电影模块的接口4方法2，以及订单模块的接口1方法1和2调用
     * quanllong
     * @param fieldId
     * @return
     */
    private String getSoldSeatsByFieldId(String fieldId){
        // 获得订单信息,要那些已经待支付和支付成功的订单，然后统计售出座位
        List<MoocOrderT> fields = moocOrderTMapper.selectList(new EntityWrapper<MoocOrderT>().eq("field_id", fieldId)
                                                                                             .notIn("order_status",OrderStatus.CLOSED.getCode()));
        // 如果一张都没卖出过，fields为空
        if(CollectionUtils.isEmpty(fields)){
            return "";
        }
        // 统计已售出的座位
        StringBuilder builder = new StringBuilder();
        for (MoocOrderT field : fields) {
            builder.append(field.getSeatsIds()).append(",");
        }
        String s = builder.toString();
        return s.substring(0,s.length() -1);
    }

    /**
     *  创建订单
     * quanllong
     * @param fieldId
     * @param seatId
     * @param seatsName
     * @param userId
     * @return
     */
    @Override
    public OrderVO saveOrderInfo(String fieldId, String[] seatId, String seatsName, Integer userId) {
        MtimeFieldT mtimeFieldT = mtimeFieldTMapper.selectById(fieldId);
        MoocOrderT moocOrderT = new MoocOrderT();
        moocOrderT.setCinemaId(mtimeFieldT.getCinemaId());
        moocOrderT.setFieldId(Integer.valueOf(fieldId));
        moocOrderT.setFilmId(mtimeFieldT.getFilmId());

        // 座位id
        String seatIdStr = Arrays.toString(seatId);
        String seatIds = seatIdStr.substring(1, seatIdStr.length() - 1).replace(", ",",");
        moocOrderT.setSeatsIds(seatIds);
        // 座位位置 第一排1座,第一排2座,第一排3座,第一排4座
        String seats = getSeatNamesBySeatIds(fieldId,seatId);
        moocOrderT.setSeatsName(seats);

        // 票单价
        moocOrderT.setFilmPrice(Double.valueOf(mtimeFieldT.getPrice()));
        // 计算总价
        moocOrderT.setOrderPrice(orderPrice(seatId, Integer.valueOf(fieldId)));

        moocOrderT.setOrderTime(new Date());
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderStatus(OrderStatus.NOT_PAY.getCode());   // 默认是0，表示未支付
        String orderId = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
        moocOrderT.setUuid(orderId);

        boolean insert = moocOrderT.insert();   // 竟然有这个方法，暂时不需要MoocOrderTMapper

        OrderVO orderVO = null;
        if(insert){
            log.info("创建订单成功，orderId:{}",orderId);
            orderVO = moocOrderT2orderVO(moocOrderT);
        }

        // 发送延时消息,用户超时未支付会被关闭订单
        SendResult sendResult = producer.sendDelayOrder(orderId,userId);
        SendStatus sendStatus = sendResult.getSendStatus();
        if(sendStatus.equals(SendStatus.SEND_OK)){
            log.info("发送延时消息成功，orderId:{}",orderId);
        }

        // 将当前用户的订单存进redis
        String  key = String.format(RedisPrefixConsistant.CURRENT_ORDER,userId);
        OrderPayStatus orderPayStatus = new OrderPayStatus(orderId, OrderStatus.NOT_PAY.getCode());
        redisTemplate.opsForValue().set(key,orderPayStatus);  // (userId,orderPayStatus)

        return orderVO;
    }

    /**
     * 取出座位的位置
     * 第一排1座,第一排2座,第一排3座,第一排4座
     * quanllong
     * @param seatId
     * @return
     */
    private String getSeatNamesBySeatIds(String fieldId,String[] seatId) {
        Seats seats = seatsFromJsonFile(Integer.valueOf(fieldId));
        // 取得最后一个单人座的id
        List<List<Seats.SingleBean>> singleList = seats.getSingle();
        List<Seats.SingleBean> singleBeans = singleList.get(singleList.size() - 1);
        int lastSingleSeatId = singleBeans.get(singleBeans.size() - 1).getSeatId();
        // id小于lastSingleSeatId是单人座，大于lastSingleSeatId的是情侣座，然后分别遍历单人座和双人座的座位表
        StringBuilder builder = new StringBuilder();
        for (String s : seatId) {
            if(Integer.valueOf(s) <= lastSingleSeatId){
                List<List<Seats.SingleBean>> single = seats.getSingle();
                for (List<Seats.SingleBean> beans : single) {
                    for (Seats.SingleBean bean : beans) {
                        int seatId1 = bean.getSeatId();
                        if(seatId1 == Integer.valueOf(s)){
                            String name = bean.getRow() + "排" + bean.getColumn() + "座";
                            builder.append(name).append(" ");
                        }
                    }
                }
            } else {
                List<List<Seats.CoupleBean>> couple = seats.getCouple();
                for (List<Seats.CoupleBean> coupleBeans : couple) {
                    for (Seats.CoupleBean coupleBean : coupleBeans) {
                        int seatId1 = coupleBean.getSeatId();
                        if(seatId1 == Integer.valueOf(s)){
                            String name = coupleBean.getRow() + "排" + coupleBean.getColumn() + "座";
                            builder.append(name).append(" ");
                        }
                    }
                }
            }
        }
        return builder.toString();
    }

    /**
     * 将数据库order的bean转为前端bean
     * quanllong
     * @param moocOrderT 数据库order表对应的bean
     * @return 返回给前端的OrderVO
     */
    private OrderVO moocOrderT2orderVO(MoocOrderT moocOrderT) {
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(moocOrderT.getUuid());

        MtimeFilmT mtimeFilmT = mtimeFilmTMapper.selectById(moocOrderT.getFilmId());
        orderVO.setFilmName(mtimeFilmT.getFilmName());

        MtimeFieldT mtimeFieldT = mtimeFieldTMapper.selectById(moocOrderT.getFieldId());
        orderVO.setFieldTime(mtimeFieldT.getBeginTime());

        MtimeCinemaT mtimeCinemaT = mtimeCinemaTMapper.selectById(moocOrderT.getCinemaId());
        orderVO.setCinemaName(mtimeCinemaT.getCinemaName());

        orderVO.setSeatsName(moocOrderT.getSeatsName().replaceAll(","," "));
        orderVO.setOrderPrice(String.valueOf(moocOrderT.getOrderPrice()));
        orderVO.setOrderTimestamp(moocOrderT.getOrderTime().getTime() / 1000);  // 订单时间戳
        // 0-待支付,1-已支付,2-已关闭
        switch (moocOrderT.getOrderStatus()){
            case 0:
                orderVO.setOrderStatus("待支付");break;
            case 1:
                orderVO.setOrderStatus("已支付");break;
            case 2:
                orderVO.setOrderStatus("已关闭");break;
        }
        return orderVO;
    }

    /**
     * 计算订单总价
     * @param seatIds 作为编号
     * @param fieldId 放映场次
     * @return 返回订单总价
     */
    private Double orderPrice(String[] seatIds,Integer fieldId){
        // 取出座位表，最好把座位表存进内存或者以组件的方式存进容器1
        Seats seats = seatsFromJsonFile(fieldId);

        // 即使选一个情侣座，也会传两个座位id进来
        Integer unitPrice = mtimeFieldTMapper.selectById(fieldId).getPrice(); // 取出该放映场次的单人票价
        double totalPrice =  unitPrice * seatIds.length;        // 单价乘以座位个数
        return totalPrice;
    }

    @Override
    public List<OrderVO> getOrderByUserId(String nowPage, String pageSize, int userId) {
        // nowPage当前页，默认第一页。pageSize每页多少条，默认5条
        if (StringUtils.isEmpty(nowPage)){
            nowPage = "1";
        }
        if (StringUtils.isEmpty(pageSize)){
            pageSize = "5";
        }
        Page<MoocOrderT> tPage = new Page<>(Integer.valueOf(nowPage), Integer.valueOf(pageSize), "order_time",false);
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("order_user",userId);

        // 取出order信息
        List<MoocOrderT> moocOrderTS = moocOrderTMapper.selectPage(tPage, wrapper);
        /*if(CollectionUtils.isEmpty(moocOrderTS)){
            return null;
        }*/

        ArrayList<OrderVO> orderList = new ArrayList<>();
        for (MoocOrderT moocOrderT : moocOrderTS) {
            OrderVO orderVO = moocOrderT2orderVO(moocOrderT);
            orderList.add(orderVO);
        }

        // 取出秒杀订单
        List<OrderVO> promoOrderList = mtimePromoOrderMapper.selectPromoOrderByUserId(userId);
        for (OrderVO orderVO : promoOrderList) {
            orderVO.setOrderStatus("已支付");
            orderVO.setFilmName("秒杀订单");
        }

        orderList.addAll(promoOrderList);

        return orderList;
    }

    /**
     * 获取放映信息
     * 供影院模块接口4的方法2调用
     * quanllong
     * @param fieldId
     * @return
     */
    @Override
    public HallInfoVO getFilmFieldInfo(Integer cinemaId,Integer fieldId) {
        // 获取场次
        EntityWrapper<MtimeFieldT> wrapper = new EntityWrapper<>();
        wrapper.eq("UUID",fieldId).eq("cinema_id",cinemaId);
        List<MtimeFieldT> mtimeFieldTS = mtimeFieldTMapper.selectList(wrapper);
        // 如果为空，返回一个什么都没有的对象
        if (CollectionUtils.isEmpty(mtimeFieldTS)){
            return null;
        }

        MtimeFieldT mtimeFieldT = mtimeFieldTS.get(0);
        HallInfoVO hallInfoVO = new HallInfoVO();
        hallInfoVO.setDiscountPrice("");
        hallInfoVO.setHallFieldId(fieldId);
        hallInfoVO.setHallName(mtimeFieldT.getHallName());
        hallInfoVO.setPrice(String.valueOf(mtimeFieldT.getPrice()));

        // 获取影厅
        MtimeHallDictT mtimeHallDictT = mtimeHallDictTMapper.selectById(mtimeFieldT.getHallId());
        // 影厅的座位表
        hallInfoVO.setSeatFile(mtimeHallDictT.getSeatAddress()); // mtime_hall_dict_t
        // 影厅的已售座位
        hallInfoVO.setSoldSeats(getSoldSeatsByFieldId(String.valueOf(fieldId)));
        return hallInfoVO;
    }

    @Override
    public int updateOrderStatus(String orderId, int status) {
        int update = moocOrderTMapper.updateStatusByUuid(orderId,status);
        return update;
    }

    @Override
    public Boolean checkOrdinaryOrderStatus(String orderId) {
        Integer code = moocOrderTMapper.selectOrderStatusByOrderId(orderId);
        if (code == OrderStatus.PAY_SUCCESS.getCode()){
            return true;    // 已支付
        }
        return false;
    }
}
