package com.stylefeng.guns.rest.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
<<<<<<< HEAD
=======
import com.baomidou.mybatisplus.plugins.Page;
>>>>>>> 59eedaa2f74abb91c0752fdf96909e610f52f605
import com.google.gson.Gson;
import com.stylefeng.guns.rest.common.persistence.dao.*;
import com.stylefeng.guns.rest.common.persistence.model.*;
import com.stylefeng.guns.rest.service.OrderService;
import com.stylefeng.guns.rest.service.bena.Seats;
import com.stylefeng.guns.rest.service.vo.OrderTestVO;
import com.stylefeng.guns.rest.service.vo.ordervo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
<<<<<<< HEAD
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
=======
import java.util.*;
>>>>>>> 59eedaa2f74abb91c0752fdf96909e610f52f605

@Component
@Service(interfaceClass = OrderService.class)
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

    /**
     * @param id
     * @return
     * 这是用来测试项目有没有跑通的，与本项目关系不大
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
        // 用户可能选中多个座位，只要有一个座位不是当前影厅的座位，就返回false
        String ids = seats.getIds();
        for (String id: seatId){
            if(! ids.contains(id)){
                return false;
            }
        }
        return true;
    }


    /**
     * @param fieldId
     * @return 返回座位表对象
     * 根据传入的放映场次fieldid，获得这个影厅的座位表
     */
    private Seats seatsFromJsonFile(Integer fieldId){
        EntityWrapper<MtimeFieldT> wrapper = new EntityWrapper<>();
        wrapper.eq("field_id",fieldId);
        MtimeFieldT mtimeFieldT = mtimeFieldTMapper.selectById(fieldId);
        MtimeHallDictT mtimeHallDictT = mtimeHallDictTMapper.selectById(mtimeFieldT.getHallId());
        // 怎么读json文件
        // InputStream stream = this.getClass().getClassLoader().getResourceAsStream("seats.123214.json");
        ClassPathResource classPathResource = new ClassPathResource(mtimeHallDictT.getSeatAddress());
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
        Gson gson = new Gson();
        Seats seats = gson.fromJson(jsonStr, Seats.class);
        return seats;
    }

    /**
     * @param fieldId 放映场次id
     * @param seatId 座位id
     * @return 座位如果被售出，返回true
     */
    @Override
    public Boolean isSoldSeats(String fieldId, String[] seatId) {
        List<MoocOrderT> fields = moocOrderTMapper.selectList(new EntityWrapper<MoocOrderT>().eq("field_id", fieldId));
        StringBuilder builder = new StringBuilder();
        for (MoocOrderT field : fields) {
            builder.append(field.getSeatsIds()).append(",");
        }
        String soldSeats = builder.toString();
        for (String s : seatId) {
            if(soldSeats.contains(s)){
                return true;
            }
        }
        return false;
    }

    /**
     * quanllong
     * @param fieldId
     * @param seatId
     * @param seatsName
     * @param userId
     * @return
     * 创建订单
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

<<<<<<< HEAD
        moocOrderT.setFilmPrice(Double.valueOf(mtimeFieldT.getPrice()));    // 票单价
        moocOrderT.setOrderPrice(orderPrice(seatId, Integer.valueOf(fieldId)));
=======
        // 票单价
        moocOrderT.setFilmPrice(Double.valueOf(mtimeFieldT.getPrice()));
        // 计算总价
        moocOrderT.setOrderPrice(orderPrice(seatId, Integer.valueOf(fieldId)));

>>>>>>> 59eedaa2f74abb91c0752fdf96909e610f52f605
        moocOrderT.setOrderTime(new Date());
        moocOrderT.setOrderUser(userId);
        moocOrderT.setOrderStatus(0);   // 默认是0，表示未支付
        String uuid = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 20);
        moocOrderT.setUuid(uuid);
        boolean insert = moocOrderT.insert();   // 竟然有这个方法，暂时不需要MoocOrderTMapper
        if(insert){
            OrderVO orderVO = moocOrderT2orderVO(moocOrderT);
            return orderVO;
        }
        return null;
    }

    /**
     * quanllong
     * @param seatId
     * @return
     * 根据座位id取出座位的位置
     * 第一排1座,第一排2座,第一排3座,第一排4座
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
     * quanllong
     * @param moocOrderT 数据库order表对应的bean
     * @return 返回给前端的OrderVO
     * 将数据库bean转为前端bean
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
        orderVO.setOrderTimestamp(String.valueOf(moocOrderT.getOrderTime().getTime()));  // 订单时间戳
        return orderVO;
    }

    /**
     * @param seatIds 作为编号
     * @param fieldId 放映场次
     * @return 返回订单总价
     */
    private Double orderPrice(String[] seatIds,Integer fieldId){
<<<<<<< HEAD
        Seats seats = seatsFromJsonFile(fieldId);
        // 取得最后一个单人座的id
        List<List<Seats.SingleBean>> singleList = seats.getSingle();
        List<Seats.SingleBean> singleBeans = singleList.get(singleList.size() - 1);
        int lastSingleSeatId = singleBeans.get(singleBeans.size() - 1).getSeatId();
        // id小于lastSingleSeatId的座位的价格是60，大于lastSingleSeatId的作为是情侣座，每个120
        Integer unitPrice = mtimeFieldTMapper.selectById(fieldId).getPrice(); // 取出该放映场次的单人票价
        double totalPrice = 0;
        for (String seatId : seatIds) {
            double price = Integer.valueOf(seatId) <= lastSingleSeatId ? unitPrice : unitPrice * 2;
            totalPrice += price;
        }
        return totalPrice;
    }

=======
        // 取出座位表，最好把座位表存进内存或者以组件的方式存进容器1
        Seats seats = seatsFromJsonFile(fieldId);

        /*// 取得最后一个单人座的id
        List<List<Seats.SingleBean>> singleList = seats.getSingle();
        List<Seats.SingleBean> singleBeans = singleList.get(singleList.size() - 1);
        int lastSingleSeatId = singleBeans.get(singleBeans.size() - 1).getSeatId();
        // id小于lastSingleSeatId的座位的价格是60，大于lastSingleSeatId的作为是情侣座，每个120*/

        // 即使选一个情侣座，也会传两个座位id进来
        Integer unitPrice = mtimeFieldTMapper.selectById(fieldId).getPrice(); // 取出该放映场次的单人票价
        double totalPrice = 0;
        /*for (String seatId : seatIds) {
            double price = Integer.valueOf(seatId) <= lastSingleSeatId ? unitPrice : unitPrice * 2;
            totalPrice += price;
        }*/
        totalPrice = unitPrice * seatIds.length;        // 单价乘以座位个数
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
        Page<MoocOrderT> tPage = new Page<>(Integer.valueOf(nowPage), Integer.valueOf(pageSize));
        EntityWrapper<MoocOrderT> wrapper = new EntityWrapper<>();
        wrapper.eq("order_user",userId);

        // 取出order信息
        List<MoocOrderT> moocOrderTS = moocOrderTMapper.selectPage(tPage, wrapper);
        if(CollectionUtils.isEmpty(moocOrderTS)){
            return null;
        }
        ArrayList<OrderVO> orderList = new ArrayList<>();
        for (MoocOrderT moocOrderT : moocOrderTS) {
            OrderVO orderVO = moocOrderT2orderVO(moocOrderT);
            orderList.add(orderVO);
        }
        return orderList;
    }
>>>>>>> 59eedaa2f74abb91c0752fdf96909e610f52f605
}
