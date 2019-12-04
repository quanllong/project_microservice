package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.promovo.PromoParams;
import com.stylefeng.guns.rest.service.vo.promovo.PromoVO;

public interface PromoService {

    int pushStockToRedis(String cinemaId);

    PromoVO getPromoInfo(PromoParams promoParams);

    int establishOrder(String promoId, String amount, Integer userId);


}
