package com.stylefeng.guns.rest.service;

import com.stylefeng.guns.rest.service.vo.promovo.PromoParams;
import com.stylefeng.guns.rest.service.vo.promovo.PromoVO;

public interface PromoService {

    boolean pushStockToRedis(String cinemaId);

    PromoVO getPromoInfo(PromoParams promoParams);

    boolean establishOrder(String promoId, String amount, Integer userId, String stockLogId);

    boolean saveOrderInfo(String promoId, String amount, Integer userId, String stockLogId);

    String initPromoStockLog(String promoId, String amount);

    Boolean establishOrderInTransaction(String promoId, String amount, Integer userId, String stockLogId);

    String generateToken(String promoId, Integer userId);
}
