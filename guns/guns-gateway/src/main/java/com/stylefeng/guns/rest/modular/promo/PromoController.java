package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.google.common.util.concurrent.RateLimiter;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.core.exception.GunsExceptionEnum;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.consistant.RedisPrefixConsistant;
import com.stylefeng.guns.rest.service.PromoService;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.promovo.ActionInfo;
import com.stylefeng.guns.rest.service.vo.promovo.PromoParams;
import com.stylefeng.guns.rest.service.vo.promovo.PromoVO;
import com.stylefeng.guns.rest.util.TokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scripting.bsh.BshScriptUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.concurrent.*;


@RequestMapping("promo")
@RestController
@Slf4j
public class PromoController {

    private static final String PUBLISH_STOCK_REIDS = "redis_stock";

    @Reference(interfaceClass = PromoService.class,check = false)
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
    public void init(){
        // 初始化一个固定数量大小的线程池
        executorService = Executors.newFixedThreadPool(100);

        // 固定每秒产生10个令牌
        // rateLimiter.setRate(100);
        rateLimiter = RateLimiter.create(10);
    }

    /*
    Request URL: http://115.29.141.32/promo/publishPromoStock
    Request Method: GET
    由前端调用
     */
    @RequestMapping("publishPromoStock")
    public BaseReqVo publishPromoStock(String cinemaId){
        String tag = (String) redisTemplate.opsForValue().get(PUBLISH_STOCK_REIDS);
        if(tag != null){
            return BaseReqVo.ok("已经发布成功，无需重复");
        }
        boolean status = promoService.pushStockToRedis(cinemaId);
        if(!status){
            return BaseReqVo.fail("发布失败");
        }
        redisTemplate.opsForValue().set(PUBLISH_STOCK_REIDS,"ok");
        redisTemplate.expire(PUBLISH_STOCK_REIDS,30, TimeUnit.SECONDS); // 30s
        return BaseReqVo.ok("发布成功");
    }

    /*
    Request URL: http://115.29.141.32/promo/getPromo?brandId=99&hallType=99&areaId=99&pageSize=12&nowPage=1
    Request Method: GET
     */
    @RequestMapping("getPromo")
    public PromoVO getPromo(PromoParams promoParams, HttpServletRequest request){
        // 不需登录
        // 先尝试从redis中取出
        PromoVO promoVO = null;
        String key = "getPromo";
        promoVO = (PromoVO) redisTemplate.opsForValue().get(key);
        if(promoVO != null){
            System.out.println("读取redis");
            return promoVO;
        }

        // 查数据库
        promoVO = promoService.getPromoInfo(promoParams);

        redisTemplate.opsForValue().set(key,promoVO);
        redisTemplate.expire(key,20,TimeUnit.MINUTES);
        System.out.println("读取redis");
        return promoVO;
    }

    /*
    Request URL: http://localhost/promo/generateToken?promoId=3
    Request Method: GET
    promoId /*秒杀活动id,必须传
     */
    @RequestMapping("generateToken")
    public BaseReqVo generateToken(@RequestParam(required = true,name = "promoId") String promoId,
                                   HttpServletRequest request){


        // 判断库存是否为空
        String emptyValue = (String) redisTemplate.opsForValue().get(RedisPrefixConsistant.EMPTY_STOCK_PREFIX + promoId);
        if (RedisPrefixConsistant.EMPTY.equals(emptyValue)){
            return BaseReqVo.fail("该商品已售罄");
        }

        // 取出userId，生成的token必须与userId关联。（开发时暂不打开）
        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if(mtimeUserVO == null){
            return BaseReqVo.fail("取出用户信息失败，请重新登录");
        }
        Integer userId = mtimeUserVO.getUuid();
        // Integer userId = 1; // 先写成固定的

        String key = String.format(RedisPrefixConsistant.USER_TOKEN_PREFIX,promoId,userId);

        // 判断之前有没有获取过（重复点击，幂等性判断）
        ActionInfo actionInfo = null;
        if(redisTemplate.hasKey(key)){
            actionInfo = (ActionInfo) redisTemplate.opsForValue().get(key);

            // 限制同一用户对同一商品重复下单
            if(RedisPrefixConsistant.HAS_BUY.equals(actionInfo.getHasBuy())){
                BaseReqVo reqVo = BaseReqVo.ok();
                reqVo.setMsg("你已购买过该商品,请选择其它商品~");
                return reqVo;
            } else {
                BaseReqVo<Object> reqVo = new BaseReqVo<>();
                reqVo.setMsg(actionInfo.getPromoToken());
                reqVo.setAnotherMsg("已经取得购买资格，请速速下单吧~~");
                return reqVo;
            }

            // 不做限制
//            BaseReqVo<Object> reqVo = new BaseReqVo<>();
//            reqVo.setMsg(actionInfo.getPromoToken());
//            reqVo.setAnotherMsg("已经取得购买资格，请速速下单吧~~");
//            return reqVo;
        }

        String promoToken = promoService.generateToken(promoId,userId);

        // 存入loginToken
        if(promoToken != null){
            String loginToken = tokenUtils.getFrontToken(request);
            ActionInfo newActionInfo  = (ActionInfo) redisTemplate.opsForValue().get(key);
            newActionInfo.setLoginToken(loginToken);
            redisTemplate.opsForValue().set(key,newActionInfo);
        }

        if(StringUtils.isBlank(promoToken)){
            return BaseReqVo.fail("获取令牌失败");
        }
        return BaseReqVo.ok(promoToken);

    }


    /*
    http://localhost/promo/createOrder?promoId=1&amount=1
    /promo/createOrder
     */
    @RequestMapping("createOrder")
    public BaseReqVo createOrder(@RequestParam(required = true,name = "promoId") String promoId,
                                 @RequestParam(required = true,name = "amount") String amount,
                                 @RequestParam(required = true,name = "promoToken") String promoToken,
                                 HttpServletRequest request){

        // 通过rateLimiter去限流
        double acquire = rateLimiter.acquire();
        if(acquire < 0){
            return BaseReqVo.fail("秒杀失败");
        }


        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if(mtimeUserVO == null){
            return BaseReqVo.fail("请先登录");
        }
        Integer userId = mtimeUserVO.getUuid();
        // Integer userId = 1;

        // 参数校验
        if(Integer.valueOf(amount) < 0 || Integer.valueOf(amount) > 5){
            return BaseReqVo.fail("amount不合法");
        }

        // 判断权限token,避免用户点击同一链接对同一个商品重复下单
        String key = String.format(RedisPrefixConsistant.USER_TOKEN_PREFIX, promoId, userId);

        Boolean aBoolean1 = redisTemplate.hasKey(key);
        if(!aBoolean1){
            return BaseReqVo.fail("秒杀令牌不存在或已过期");
        }
        ActionInfo actionInfo = null;
        Object o = redisTemplate.opsForValue().get(key);
        if(o instanceof ActionInfo){
            actionInfo = (ActionInfo) o;
        }

        if(RedisPrefixConsistant.HAS_BUY.equals(actionInfo.getHasBuy())){
            return BaseReqVo.fail("您已秒杀过，请选择其它商品");
        }

        if(!promoToken.equals(actionInfo.getPromoToken())){
            return BaseReqVo.fail("秒杀令牌不合法");
        }


        // 普通方式
        // int status = promoService.establishOrder(promoId,amount,userId);

        // 使用分布式事务
        Future<Boolean> future = executorService.submit(() -> {
            Boolean result = false;
            try{
                // 首先初始化一条流水记录
                String stockLogId = promoService.initPromoStockLog(promoId, amount);
                if (StringUtils.isEmpty(stockLogId)) {
                    log.info("流水表创建失败，未创建订单，promoId:{},userId:{},amount:{}",promoId,userId,amount);
                    throw new GunsException(GunsExceptionEnum.STOCK_LOG_ERROR);
                }
                // 去新建订单
                result = promoService.establishOrderInTransaction(promoId, amount, userId, stockLogId);
                if(!result){
                    log.info("创建订单失败");
                    throw new GunsException(GunsExceptionEnum.CREATE_ORDER_ERROR);
                }
            } catch (Exception e){
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

        if(aBoolean){
            // 更改令牌状态
            actionInfo.setHasBuy(RedisPrefixConsistant.HAS_BUY);
            redisTemplate.opsForValue().set(key,actionInfo);
            log.info("秒杀成功且改变hasBuy，key:{},hasBuy:{}",key,RedisPrefixConsistant.HAS_BUY);
            return BaseReqVo.ok("秒杀成功，请查看订单");
        }
        return BaseReqVo.fail("人气太旺了，请稍等~~");
    }

}
