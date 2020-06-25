package com.stylefeng.guns.rest.util;

import com.stylefeng.guns.rest.BaseReqVo;
import com.stylefeng.guns.rest.common.persistence.model.MtimeUserT;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 解析request，获得用户信息
 * quanllong
 */
@Component
public class TokenUtils {

    @Autowired
    RedisTemplate redisTemplate;    // 这个redisTemplate必须用自己配置的

    public MtimeUserVO parseRequest(HttpServletRequest request){

        String token = getFrontToken(request);
        MtimeUserVO user = (MtimeUserVO) redisTemplate.opsForValue().get(token);
        return user;
    }

    // 取得前端传来的token
    public String getFrontToken(HttpServletRequest request){
        /*String authorization = request.getHeader("Authorization");
        if(authorization == null || "".equals(authorization)){
            return null;
        }
        return authorization.substring(7);*/
        return "eyJhbGciOiJIUzUxMiJ9.eyJyYW5kb21LZXkiOiI3ZmpvaGsiLCJzdWIiOiJ1c2VyMSIsImV4cCI6MTU3OTI1MDM2NywiaWF0IjoxNTc4NjQ1NTY3fQ.XKll6TgeMyf9uecCKrFHTpAc0GxwLkh5GWvloyiQuvSWb3fggkCKFuLgTVkbtaEZLOpc-LIliR3GKu9PeeGj1w";
    }

}
