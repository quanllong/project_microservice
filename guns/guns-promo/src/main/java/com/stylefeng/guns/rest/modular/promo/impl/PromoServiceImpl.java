package com.stylefeng.guns.rest.modular.promo.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.core.util.UUIDUtils;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoOrderMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimePromoStockMapper;
import com.stylefeng.guns.rest.common.persistence.dao.MtimeStockLogMapper;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromo;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoOrder;
import com.stylefeng.guns.rest.common.persistence.model.MtimePromoStock;
import com.stylefeng.guns.rest.common.persistence.model.MtimeStockLog;
import com.stylefeng.guns.rest.modular.promo.bean.Stock;
import com.stylefeng.guns.rest.mq.MqProducer;
import com.stylefeng.guns.rest.service.CinemaService;
import com.stylefeng.guns.rest.service.PromoService;
import com.stylefeng.guns.rest.service.vo.cinemavo.CinemaInfoVO;
import com.stylefeng.guns.rest.service.vo.promovo.PromoData;
import com.stylefeng.guns.rest.service.vo.promovo.PromoParams;
import com.stylefeng.guns.rest.service.vo.promovo.PromoVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
@Slf4j
@Service(interfaceClass = PromoService.class)
public class PromoServiceImpl implements PromoService {

    // 存放mtime_promo_stock表的数据
    private static final String REDIS_MTIME_STOCK_PREFIX = "mtime_promo_stock_";

    // 存放mtime_promo表的数据
    private static final String REDIS_MTIME_PROMO_PREFIX = "mtime_promo_";

    /*@Reference(interfaceClass = CinemaService.class,check = false)
    CinemaService cinemaService;*/

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
    @Autowired
    MtimeStockLogMapper mtimeStockLogMapper;

    private ExecutorService executorService;

    @PostConstruct
    public void init(){
        //创建一个线程池，里面有十个线程
        executorService  = Executors.newFixedThreadPool(100);
    }

    /**
     * 点击秒杀，首先发布库存到redis
     * @param cinemaId
     * @return
     */
    @Override
    public boolean pushStockToRedis(String cinemaId) {
        // 获取活动信息
        // 联合查询
        List<MtimePromoStock> promoStocks =  mtimePromoStockMapper.selectStockByCinemaIdAndPromoId(cinemaId,null);
        for (MtimePromoStock promoStock : promoStocks) {
            String promoId = String.valueOf(promoStock.getPromoId());
            String stock = String.valueOf(promoStock.getStock());
            // 存到redis
            redisTemplate.opsForValue().set(REDIS_MTIME_STOCK_PREFIX + promoId,stock);
        }
        return true;
    }

    /**
     * 获取秒杀信息
     * 这个步骤是publishPromoStock之后执行的，所以应该从redis中读库存
     * @param promoParams
     * @return
     */
    @Override
    public PromoVO getPromoInfo(PromoParams promoParams) {
        // 先尝试从缓存中取
        //

        // 查出影院信息,这里要优化为联合查询，而且不要用cinemaService的方法
        List<PromoData> promoDataList = mtimePromoMapper.queryPromoDataByCinemaId(null,null);
        for (PromoData promoData : promoDataList) {

            // 从redis中取出库存
            String promoId = String.valueOf(promoData.getUuid());
            int stock = getStockFromRedis(promoId);
            promoData.setStock(stock);    // (promoId，stock)

            // 顺手把秒杀信息加进redis，因为创建订单还要用到这些信息
            redisTemplate.opsForValue().set(REDIS_MTIME_PROMO_PREFIX + promoId,promoData);
        }

        PromoVO promoVO = new PromoVO();
        // 计算总页数
        Integer pageSize = promoParams.getPageSize();
        if(pageSize != null){
            int size = promoDataList.size();
            int totalPage = size % pageSize == 0 ? size / pageSize : size / pageSize + 1;
            promoVO.setTotalPage(String.valueOf(totalPage));
        }

        promoVO.setData(promoDataList);
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
    private int getStockFromRedis(String promoId) {
        Object o = redisTemplate.opsForValue().get(REDIS_MTIME_STOCK_PREFIX + promoId);
        // 如果为null,就直接查数据库
        if(o == null){
            MtimePromoStock mtimePromoStock = mtimePromoStockMapper.queryStockByPromoId(promoId);
            return mtimePromoStock.getStock();
        }
        if(o instanceof String){
            String stock = (String) o;
            return Integer.valueOf(stock);
        }
        if(o instanceof Integer){
            return (Integer) o;
        }
        return 0;
    }


    @Override
    public boolean establishOrder(String promoId, String amount, Integer userId,String stockLogId) {

        // 下单前先检查库存还有没有
        Integer stock = (Integer) redisTemplate.opsForValue().get(REDIS_MTIME_STOCK_PREFIX + promoId);
        if(stock == null){
            return false;  // 未知异常
        }
        if(stock < Integer.valueOf(amount)){
            return false;
        }

        // 保存订单信息
        boolean flag = saveOrderInfo(promoId, amount, userId,stockLogId);
        if(!flag){
            return false;
        }

        // redis要把缓存告诉数据库，amount 是被减去的量
        Boolean aBoolean = mqProducer.decreaseStock(Integer.valueOf(promoId), stock - Integer.valueOf(amount));
        if(aBoolean){
            return false;
        }
        return true;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public boolean saveOrderInfo(String promoId, String amount, Integer userId,String stockLogId){
        // 订单入库
        boolean insertPromoOrder = insertPromoOrder(promoId, amount, userId);
        if (! insertPromoOrder){
            log.info("订单入库失败");
            // 更新流水表
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    int status = mtimeStockLogMapper.updateStatusById(stockLogId, 2);   // 能返回1吗
                    log.info("订单入库失败 ，流水表状态已经更改 。status = {}",2);
                }
            });
            throw new GunsException(GunsExceptionEnum.SERVER_ERROR);
        }

        // 更改redis缓存
        boolean update = updateRedisStock(promoId,amount);
        if(!update){
            executorService.submit(new Runnable() {
                @Override
                public void run() {
                    int status = mtimeStockLogMapper.updateStatusById(stockLogId, 2);   // 能返回1吗
                    log.info("更新redis缓存失败 ，流水表状态已经更改 。status = {}",2);
                }
            });
            throw new GunsException(GunsExceptionEnum.REDIS_ERROR);
        }
        // 表示成功，更改流水表
        mtimeStockLogMapper.updateStatusById(stockLogId, 1);
        log.info("创建订单和更改redis缓存成功，流水表状态更改为1");
        return true;
    }

    private boolean updateRedisStock(String promoId, String amount) {
        // 扣减要一步到位
        Long increment = redisTemplate.opsForValue().increment(REDIS_MTIME_STOCK_PREFIX+promoId, Integer.valueOf(amount) * -1);
        if(increment < 0){
            log.info("redis库存不足,扣减失败 promoId={}",promoId);
            // 手动回滚redis
            Long rollbackValue = redisTemplate.opsForValue().increment(REDIS_MTIME_STOCK_PREFIX+promoId, Integer.valueOf(amount));
            log.info("redis回滚后的值：{}",rollbackValue);
            return false;
        }
        return true;
    }

    /**
     * 订单入库
     * @param promoId
     * @param amount
     * @param userId
     * @return
     */
    private boolean insertPromoOrder(String promoId, String amount, Integer userId) {

        MtimePromoOrder mtimePromoOrder = new MtimePromoOrder();
        String uuid = UUID.randomUUID().toString().substring(0, 7);
        mtimePromoOrder.setUuid(uuid);
        mtimePromoOrder.setUserId(userId);

        // 先尝试从redis中取出PromoData,如果没有就去查数据库
        PromoData currentPromo = (PromoData) redisTemplate.opsForValue().get(REDIS_MTIME_PROMO_PREFIX + promoId);
        if(currentPromo == null){
            List<PromoData> promoData = mtimePromoMapper.queryPromoDataByCinemaId(null, promoId);
            currentPromo = promoData.get(0);
        }

        mtimePromoOrder.setCinemaId(currentPromo.getCinemaId());
        mtimePromoOrder.setExchangeCode("abc");     // 随便写的
        mtimePromoOrder.setAmount(Integer.valueOf(amount));
        mtimePromoOrder.setPrice(currentPromo.getPrice());
        mtimePromoOrder.setStartTime(currentPromo.getStartTime());
        mtimePromoOrder.setCreateTime(new Date());
        mtimePromoOrder.setEndTime(currentPromo.getEndTime());

        boolean insert = mtimePromoOrder.insert();
        return insert;
    }

    @Override
    public String initPromoStockLog(String promoId, String amount) {
        // 初始化库存流水表
        MtimeStockLog mtimeStockLog = new MtimeStockLog();
        String stockLogId = UUID.randomUUID().toString().substring(0,10);
        mtimeStockLog.setUuid(stockLogId);
        mtimeStockLog.setPromoId(Integer.valueOf(promoId));
        mtimeStockLog.setAmount(Integer.valueOf(amount));
        // 0初始值，1成功，2失败
        mtimeStockLog.setStatus(0);
        boolean insert = mtimeStockLog.insert();
        // 返回id
        return stockLogId;
    }

    /**
     * 使用分布式事务新建订单
     * quanllong
     * @param promoId
     * @param amount
     * @param userId
     * @param stockLogId
     * @return
     */
    @Override
    public Boolean establishOrderInTransaction(String promoId, String amount, Integer userId,String stockLogId) {
        // 调用mq的方法
        return mqProducer.savePromoInfoInTransaction(promoId,amount,userId,stockLogId);
    }
}
