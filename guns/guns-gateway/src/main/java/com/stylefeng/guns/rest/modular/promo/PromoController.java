package com.stylefeng.guns.rest.modular.promo;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.service.PromoService;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import com.stylefeng.guns.rest.service.vo.promovo.PromoParams;
import com.stylefeng.guns.rest.service.vo.promovo.PromoVO;
import com.stylefeng.guns.rest.util.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequestMapping("promo")
@RestController
public class PromoController {
    @Reference(interfaceClass = PromoService.class,check = false)
    PromoService promoService;

    @Autowired
    TokenUtils tokenUtils;

    /*
    Request URL: http://115.29.141.32/promo/publishPromoStock
    Request Method: GET
    由前端调用
     */
    @RequestMapping("publishPromoStock")
    public BaseReqVo publishPromoStock(String cinemaId){
        int status = promoService.pushStockToRedis(cinemaId);
        BaseReqVo<Object> reqVo = new BaseReqVo<>();
        reqVo.setMsg("发布成功");
        return reqVo;
    }

    /*
    Request URL: http://115.29.141.32/promo/getPromo?brandId=99&hallType=99&areaId=99&pageSize=12&nowPage=1
    Request Method: GET
     */
    @RequestMapping("getPromo")
    public PromoVO getPromo(PromoParams promoParams, HttpServletRequest request){
        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if(mtimeUserVO == null){
            PromoVO promoVO = new PromoVO();
            promoVO.setMsg("请先登录"); // 测试前端是否能显示出来
            return null;
        }
        PromoVO promoVO = promoService.getPromoInfo(promoParams);
        return promoVO;
    }

    /*
    /promo/createOrder
     */
    @RequestMapping("createOrder")
    public BaseReqVo createOrder(String promoId,String amount,HttpServletRequest request){
        MtimeUserVO mtimeUserVO = tokenUtils.parseRequest(request);
        if(mtimeUserVO == null){
            return BaseReqVo.fail("请先登录");
        }
        Integer userId = mtimeUserVO.getUuid();
        int status = promoService.establishOrder(promoId,amount,userId);
        if(status == 1){
            return BaseReqVo.ok("秒杀成功，请查看订单");
        }
        return BaseReqVo.fail("人气太旺了，请稍等~~");
    }

}
