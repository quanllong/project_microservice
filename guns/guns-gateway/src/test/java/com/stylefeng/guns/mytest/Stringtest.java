package com.stylefeng.guns.mytest;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class Stringtest {
    @Test
    public void test(){
        String ignoreUrl = "/auth,/order/getOrderInfo,/user/logout,/user/check,/user/register,/film/getIndex,/cinema/getFields";
        String[] strings = ignoreUrl.split(",");

        String requestUrl = "order/getOrderInfo";   // 这里要检查一下request.get
        for (String string : strings) {
            if(string.equals(requestUrl)){
                log.info("允许{}访问，放行",requestUrl);
            }
        }
        log.info("需要登录验证");
    }
}
