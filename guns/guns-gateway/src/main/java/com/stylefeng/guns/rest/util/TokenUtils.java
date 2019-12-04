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
        String authorization = request.getHeader("Authorization");
        if(authorization == null || "".equals(authorization)){
            return null;
        }
        String token = authorization.substring(7);
        MtimeUserVO user = (MtimeUserVO) redisTemplate.opsForValue().get(token);
        return user;
    }
}
