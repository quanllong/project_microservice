package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.util.concurrent.RateLimiter;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.consistant.RedisPrefixConsistant;
import com.stylefeng.guns.rest.consistant.RedisStatus;
import com.stylefeng.guns.rest.service.PromoService;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.promovo.ActionInfo;
import com.stylefeng.guns.rest.service.vo.promovo.PromoData;
import com.stylefeng.guns.rest.service.vo.promovo.PromoParams;
import com.stylefeng.guns.rest.service.vo.promovo.PromoVO;
import com.stylefeng.guns.rest.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;


/**
 * @author quanlinglong
 */
@RequestMapping("promo")
@RestController
@Slf4j
public class PromoController {

    private static final String PUBLISH_STOCK_REIDS = "redis_stock";

    @Reference(interfaceClass = PromoService.class, check = false)
    PromoService promoService;

    @Autowired
    TokenUtils tokenUtils;
    @Autowired
    RedisTemplate redisTemplate;

    // 声明一个线程池
    private ExecutorService executorService;

    // 声明一个令牌桶
    private RateLimiter rateLimiter;

    @PostConstruct
    public void init() {
        // 初始化一个固定数量大小的线程池
        executorService = Executors.newFixedThreadPool(20);

        // 固定每秒产生10个令牌
        rateLimiter = RateLimiter.create(300);
    }

    /*
    Request URL: http://115.29.141.32/promo/publishPromoStock
    Request Method: GET
    前端首先调用publishPromoStock,后台需要的操作是把数据库的库存发布到redis缓存中
    紧接着前端调用getPromo
    点击马上抢后，调用两个接口
    先调用generateToken，返回一个token令牌
    最后调用createOrder
    createOrder执行成功后会再调用getPromo接口
     */
    @RequestMapping(value = "publishPromoStock")
    public BaseReqVo publishPromoStock(String cinemaId) {
        String tag = (String) redisTemplate.opsForValue().get(PUBLISH_STOCK_REIDS);
        if (tag != null) {
            return BaseReqVo.ok("已经发布成功，无需重复");
        }

        // 调用业务层的接口将库存信息发布到缓存中
        boolean status = promoService.pushStockToRedis(cinemaId);

        if (!status) {
            return BaseReqVo.fail("发布失败");
        }

        redisTemplate.opsForValue().set(PUBLISH_STOCK_REIDS, "ok");

        // 设置商品库存的过期时间，一旦过期，需要重新访问数据库去获取。目的是及时将数据库的信息更新到redis中
        redisTemplate.expire(PUBLISH_STOCK_REIDS, 30, TimeUnit.SECONDS); // 30s

        return BaseReqVo.ok("发布成功");
    }

    /*
    Request URL: http://115.29.141.32/promo/getPromo?brandId=99&hallType=99&areaId=99&pageSize=12&nowPage=1
    Request Method: GET
     */
    @RequestMapping("getPromo")
    public PromoVO getPromo(PromoParams promoParams, HttpServletRequest request) {

        // 先尝试从redis中取出页面信息
        if (redisTemplate.hasKey(RedisStatus.PROMOVO)) {

            PromoVO promoVO = (PromoVO) redisTemplate.opsForValue().get(RedisStatus.PROMOVO);
            List<PromoData> promoDatas = promoVO.getData();

            // 从redis中读取最新的库存信息,并更新到要返回的的VO类中
            for (PromoData promoData : promoDatas) {
                Integer amount = (Integer) redisTemplate.opsForValue().get(RedisStatus.REDIS_MTIME_STOCK_PREFIX + promoData.getPromoId());
                promoData.setStock(amount);
            }

            return promoVO;
        }

        // 查数据库
        PromoVO promoVO = promoService.getPromoInfo(promoParams);

        // 存进redis
        redisTemplate.opsForValue().set(RedisStatus.PROMOVO, promoVO);

        return promoVO;
    }

    /*
    Request URL: http://localhost/promo/generateToken?promoId=3
    Request Method: GET

    promoId  秒杀活动id,必须传
     */
    @RequestMapping("generateToken")
    public BaseReqVo generateToken(@RequestParam(required = true, name = "promoId") String promoId,
                                   HttpServletRequest request) {

        // 判断库存是否为空
        String emptyValue = (String) redisTemplate.opsForValue().get(RedisPrefixConsistant.EMPTY_STOCK_PREFIX + promoId);
        if (RedisPrefixConsistant.EMPTY.equals(emptyValue)) {
            return BaseReqVo.fail("该商品已售罄");
        }

        // 取出userId，生成的token必须与userId关联
        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if (mtimeUserVO == null) {
            return BaseReqVo.fail("取出用户信息失败，请重新登录");
        }
        Integer userId = mtimeUserVO.getUuid();
        // Integer userId = 1; // 开发时写成固定的

        String key = String.format(RedisPrefixConsistant.USER_TOKEN_PREFIX, promoId, userId);

        // 判断之前有没有获取过（重复点击，幂等性判断）
        ActionInfo actionInfo = null;
        if (redisTemplate.hasKey(key)) {
            actionInfo = (ActionInfo) redisTemplate.opsForValue().get(key);

            // 限制同一用户对同一商品重复下单
//            if(RedisPrefixConsistant.HAS_BUY.equals(actionInfo.getHasBuy())){
//                BaseReqVo reqVo = BaseReqVo.ok();
//                reqVo.setMsg("你已购买过该商品,请选择其它商品~");
//                return reqVo;
//            } else {
//                BaseReqVo<Object> reqVo = new BaseReqVo<>();
//                reqVo.setMsg(actionInfo.getPromoToken());
//                reqVo.setAnotherMsg("已经取得购买资格，请速速下单吧~~");
//                return reqVo;
//            }

        }

        // 获取令牌
        String promoToken = promoService.generateToken(promoId, userId);

        if (StringUtils.isBlank(promoToken)) {
            return BaseReqVo.fail("获取令牌失败");
        }
        return BaseReqVo.ok(promoToken);

    }


    /*
    Request URL: http://localhost/promo/createOrder?promoId=3&amount=1&promoToken=65640edf716845358b
    Request Method: POST
     */
    @RequestMapping("createOrder")
    public BaseReqVo createOrder(@RequestParam(required = true, name = "promoId") String promoId,
                                 @RequestParam(required = true, name = "amount") String amount,
                                 @RequestParam(required = true, name = "promoToken") String promoToken,
                                 HttpServletRequest request) {

        // 通过rateLimiter去限流
        double acquire = rateLimiter.acquire();
        if (acquire < 0) {
            return BaseReqVo.fail("秒杀失败");
        }

        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if (mtimeUserVO == null) {
            return BaseReqVo.fail("请先登录");
        }
        Integer userId = mtimeUserVO.getUuid();
//         Integer userId = 1;

        // 校验下单数量
        if (Integer.parseInt(amount) < 0 || Integer.parseInt(amount) > 5) {
            return BaseReqVo.fail("amount不合法");
        }

        // 判断权限token,避免用户点击同一链接对同一个商品重复下单
        String key = String.format(RedisPrefixConsistant.USER_TOKEN_PREFIX, promoId, userId);

        if (!redisTemplate.hasKey(key)) {
            return BaseReqVo.fail("秒杀令牌不存在或已过期");
        }

        ActionInfo actionInfo = null;
        Object o = redisTemplate.opsForValue().get(key);
        if (o instanceof ActionInfo) {
            actionInfo = (ActionInfo) o;
        }

//        if(RedisPrefixConsistant.HAS_BUY.equals(actionInfo.getHasBuy())){
//            return BaseReqVo.fail("您已秒杀过，请选择其它商品");
//        }

        // 校验前端传来的token与缓存中的token
        if (!promoToken.equals(actionInfo.getPromoToken())) {
            return BaseReqVo.fail("秒杀令牌不合法");
        }


        // 使用本地事务
//         int status = promoService.establishOrder(promoId,amount,userId);

        // 使用分布式事务
        Future<Boolean> future = executorService.submit(() -> {
            Boolean result = false;
            try {
                // 首先初始化一条流水记录
                String stockLogId = promoService.initPromoStockLog(promoId, amount);
                if (StringUtils.isEmpty(stockLogId)) {
                    log.info("流水表创建失败，未创建订单，promoId:{},userId:{},amount:{}", promoId, userId, amount);
                    throw new GunsException(GunsExceptionEnum.STOCK_LOG_ERROR);
                }
                // 去新建订单
                result = promoService.establishOrderInTransaction(promoId, amount, userId, stockLogId);
                if (!result) {
                    log.info("创建订单失败");
                    throw new GunsException(GunsExceptionEnum.CREATE_ORDER_ERROR);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        });


        // 线程池执行结果
        Boolean aBoolean = false;
        try {
            aBoolean = future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        if (aBoolean) {
            // 更改令牌状态
            actionInfo.setHasBuy(RedisPrefixConsistant.HAS_BUY);
            redisTemplate.opsForValue().set(key, actionInfo);
            log.info("秒杀成功且改变hasBuy，key:{},hasBuy:{}", key, RedisPrefixConsistant.HAS_BUY);
            return BaseReqVo.ok("秒杀成功，请查看订单");
        }
        return BaseReqVo.fail("人气太旺了，请稍等~~");
    }

}
