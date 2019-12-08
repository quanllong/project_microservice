package com.stylefeng.guns.rest.service.vo.promovo;

import lombok.Data;

import java.io.Serializable;

/**
 * 用来标记用户的秒杀行为
 * 主要是PromoController和PromoServiceImpl用的多
 */
@Data
public class ActionInfo implements Serializable {
    private static final long serialVersionUID = 3158213946013078615L;

    private String loginToken;  // 登录验证的token,把它和请求头的token进行比对，判断是否登录过，或者登录超时
    private String promoToken;  // 秒杀模块的generateToken
    private String hasBuy;      // 针对同一个userId和promoId，用户是否秒杀过。秒杀前记为pre，秒杀成功过就会判断记为done。用处使禁止同一用户对同一影院的秒杀活动重复秒杀

    public ActionInfo() {
    }

    public ActionInfo(String loginToken, String promoToken, String hasBuy) {
        this.loginToken = loginToken;
        this.promoToken = promoToken;
        this.hasBuy = hasBuy;
    }

    // String key = String.format(RedisPrefixConsistant.USER_TOKEN_PREFIX,promoId,userId);
}
