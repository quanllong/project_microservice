package com.stylefeng.guns.rest.modular.promo.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.stylefeng.guns.rest.modular.promo.bean.Stock;
import com.stylefeng.guns.rest.mq.MqProducer;
import com.stylefeng.guns.rest.service.CinemaService;
import com.stylefeng.guns.rest.service.PromoService;
import com.stylefeng.guns.rest.service.vo.cinemavo.CinemaInfoVO;
import com.stylefeng.guns.rest.service.vo.promovo.PromoData;
import com.stylefeng.guns.rest.service.vo.promovo.PromoParams;
import com.stylefeng.guns.rest.service.vo.promovo.PromoVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Service(interfaceClass = PromoService.class)
public class PromoServiceImpl implements PromoService {

    @Reference(interfaceClass = CinemaService.class,check = false)
    CinemaService cinemaService;

    @Autowired
    RedisTemplate redisTemplate;
    @Autowired
    MqProducer mqProducer;
    @Autowired
    MtimePromoMapper mtimePromoMapper;
    @Autowired
    MtimePromoOrderMapper mtimePromoOrderMapper;
    @Autowired
    MtimePromoStockMapper mtimePromoStockMapper;

    /**
     * 点击秒杀，首先发布库存到redis
     * @param cinemaId
     * @return
     */
    @Override
    public int pushStockToRedis(String cinemaId) {
        // 获取活动信息
        EntityWrapper<MtimePromo> promoEntityWrapper = new EntityWrapper<>();
        if(cinemaId != null && !"".equals(cinemaId)){
            promoEntityWrapper.eq("cinema_id",cinemaId);
        }
        List<MtimePromo> mtimePromos = mtimePromoMapper.selectList(promoEntityWrapper);

        // int count = 0;
        for (MtimePromo mtimePromo : mtimePromos) {
            Integer promoId = mtimePromo.getUuid();
            // 取出对应的库存
            Integer stock = mtimePromoStockMapper.selectList(new EntityWrapper<MtimePromoStock>().eq("promo_id", promoId)).get(0).getStock();
            // 存到redis
            redisTemplate.opsForHash().put("promoId--stock",promoId,stock);
            // count++;
        }
        return 1;
    }

    /**
     * 获取秒杀信息
     * 这个步骤是publishPromoStock之后执行的，所以应该从redis中读库存
     * @param promoParams
     * @return
     */
    @Override
    public PromoVO getPromoInfo(PromoParams promoParams) {
        EntityWrapper<MtimePromo> wrapper = new EntityWrapper<>();
        List<MtimePromo> mtimePromos = mtimePromoMapper.selectList(wrapper);

        PromoVO promoVO = new PromoVO();
        ArrayList<PromoData> list = new ArrayList<>();

        for (MtimePromo mtimePromo : mtimePromos) {
            PromoData promoData = new PromoData();
            // 查出影院信息
            CinemaInfoVO cinemaInfo = cinemaService.getCinemaInfoById(mtimePromo.getCinemaId());
            promoData.setCinemaAddress(cinemaInfo.getCinemaAdress());
            promoData.setCinemaId(cinemaInfo.getCinemaId());
            promoData.setCinemaName(cinemaInfo.getCinemaName());

            promoData.setDescription(mtimePromo.getDescription());
            promoData.setEndTime(mtimePromo.getEndTime());
            promoData.setImgAddress(cinemaInfo.getImgUrl());
            promoData.setPrice(mtimePromo.getPrice());
            promoData.setStatus(mtimePromo.getStatus());
            Integer promoId = mtimePromo.getUuid();
            promoData.setUuid(promoId);

            // 从redis中取出库存 ,
            int stock = getStockFromRedis(mtimePromo.getUuid());
            promoData.setStock(stock);    // 库存怎么查，先写成固定的。。。这个应该从redis中读，(promoId，stock)

            // 加进列表
            list.add(promoData);

            // 顺手把秒杀信息加进redis，因为创建订单还要用到这些信息
            redisTemplate.opsForHash().put("promoId--mtimePromo",promoId,mtimePromo);

        }
        Integer pageSize = promoParams.getPageSize();
        int size = mtimePromos.size();
        int totalPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;
        promoVO.setData(list);
        promoVO.setTotalPage(String.valueOf(totalPage));
        promoVO.setImgPre("");
        promoVO.setStatus(0);
        return promoVO;
    }

    /**
     * 从reids中读库存
     * 如果读出stock为null，返回0
     * @param promoId
     * @return
     */
    private int getStockFromRedis(Integer promoId) {
        Integer stock = (Integer) redisTemplate.opsForHash().get("promoId--stock",promoId);
        if(stock != null){
            return stock;
        }
        return 0;
    }

    @Override
    public int establishOrder(String promoId, String amount, Integer userId) {

        // 下单前先检查库存还有没有
        Integer stock = (Integer) redisTemplate.opsForHash().get("promoId--stock", promoId);
        if(stock == null){
            return -1;  // 未知异常
        }
        if(stock < Integer.valueOf(amount)){
            return stock;
        }

        // 创建订单
        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
        String uuid = UUID.randomUUID().toString().substring(0, 7);
        mtimePromoOrder.setUuid(uuid);
        mtimePromoOrder.setUserId(userId);

        // 取出秒杀活动
        /*List<MtimePromo> mtimePromos = mtimePromoMapper.selectList(new EntityWrapper<MtimePromo>().eq("UUID", promoId));
        MtimePromo currentPromo = mtimePromos.get(0);*/
        // 改用redis
        MtimePromo currentPromo = (MtimePromo) redisTemplate.opsForHash().get("promoId--mtimePromo", promoId);

        mtimePromoOrder.setCinemaId(currentPromo.getCinemaId());
        mtimePromoOrder.setExchangeCode("abc");     // 随便写的
        mtimePromoOrder.setAmount(Integer.valueOf(amount));
        mtimePromoOrder.setPrice(currentPromo.getPrice());
        mtimePromoOrder.setStartTime(currentPromo.getStartTime());
        mtimePromoOrder.setCreateTime(new Date());
        mtimePromoOrder.setEndTime(currentPromo.getEndTime());

        // 更改redis缓存
        redisTemplate.opsForHash().put("promoId--stock",promoId,stock - Integer.valueOf(amount));

        // redis要把缓存告诉数据库
        Boolean aBoolean = mqProducer.decreaseStock(Integer.valueOf(promoId), stock);
        if(aBoolean){
            return 1;
        }
        return 0;
    }
}
