package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.PromoService;
import com.stylefeng.guns.rest.service.vo.promovo.PromoParams;
import com.stylefeng.guns.rest.service.vo.promovo.PromoVO;
import com.stylefeng.guns.rest.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.TimeUnit;


@RequestMapping("promo")
@RestController
public class PromoController {

    private static final String PUBLISH_STOCK_REIDS = "redis_stock";

    @Reference(interfaceClass = PromoService.class,check = false)
    PromoService promoService;

    @Autowired
    TokenUtils tokenUtils;
    @Autowired
    RedisTemplate redisTemplate;

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
        /*MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if(mtimeUserVO == null){
            PromoVO promoVO = new PromoVO();
            promoVO.setMsg("请先登录"); // 测试前端是否能显示出来
            return null;
        }*/
        PromoVO promoVO = promoService.getPromoInfo(promoParams);
        return promoVO;
    }

    /*
    /promo/generateToken
    promoId /*秒杀活动id,必须传
     */
    /*@RequestMapping("generateToken")
    public BaseReqVo generateToken(@RequestParam(required = true,name = "promoId") String promoId){
        String token = promoService.generateToken(promoId);
        if(StringUtil)
    }*/


    /*
    http://localhost/promo/createOrder?promoId=1&amount=1
    /promo/createOrder
     */
    @RequestMapping("createOrder")
    public BaseReqVo createOrder(String promoId,String amount,HttpServletRequest request){
        // MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        /*if(mtimeUserVO == null){
            return BaseReqVo.fail("请先登录");
        }
        if(Integer.valueOf(amount) < 0 || Integer.valueOf(amount) > 5){
            return BaseReqVo.fail("amount不合法");
        }*/

        // Integer userId = mtimeUserVO.getUuid();
        Integer userId = 1;
        // 普通方式
        // int status = promoService.establishOrder(promoId,amount,userId);

        // 使用分布式事务
        // 首先初始化一条流水记录
        String stockLogId = promoService.initPromoStockLog(promoId,amount);
        if(StringUtils.isEmpty(stockLogId)){
            return BaseReqVo.fail("创建订单失败");
        }
        // 去新建订单
        Boolean result = promoService.establishOrderInTransaction(promoId,amount,userId,stockLogId);
        if(result){
            return BaseReqVo.ok("秒杀成功，请查看订单");
        }
        return BaseReqVo.fail("人气太旺了，请稍等~~");
    }

}
