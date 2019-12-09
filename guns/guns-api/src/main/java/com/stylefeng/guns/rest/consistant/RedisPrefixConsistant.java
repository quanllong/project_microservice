package com.stylefeng.guns.rest.consistant;

public class RedisPrefixConsistant {


    //库存售罄缓存key前缀,   售罄
    public static final String EMPTY_STOCK_PREFIX = "empty_promoid_";

    // 卖完
    public static final String NOT_EMPTY = "notEmpty";

    // 还没卖完
    public static final String EMPTY = "empty";

    //秒杀令牌token存放缓存前缀，  用户令牌
    public static String USER_TOKEN_PREFIX = "user_promoId_%s_userId_%s";

    //秒杀令牌数量限制缓存前缀，  令牌数量
    public static String TOKEN_STOCK_PREFIX = "promo_token_limit_";

    // 买成功
    public static final String HAS_BUY = "done" ;

    // 买失败
    public static final String NOT_BUY = "pre";

    // 当前用户普通订单，orderService创建，payServiceImpl用于检查，orderController创建普通订单前也要检查，payServiceImpl更新订单转态后也要更新该值，Order中的consumer关闭订单后也要更新该值
    public static final String CURRENT_ORDER = "mtime_order_current_userId_%s";

    /*
    // 更新redis
    String key = String.format(RedisPrefixConsistant.CURRENT_ORDER,userId);
    OrderPayStatus orderPayStatus = new OrderPayStatus(orderId, OrderStatus.CLOSED.getCode());
    redisTemplate.opsForValue().set(key,orderPayStatus);
     */
}
