package com.stylefeng.guns.rest.modular.auth.filter;

import com.stylefeng.guns.core.base.tips.ErrorTip;
import com.stylefeng.guns.core.util.RenderUtil;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.config.properties.JwtProperties;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import io.jsonwebtoken.JwtException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 对客户端请求的jwt token验证过滤器
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:04
 */
public class AuthFilter extends OncePerRequestFilter {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        /*忽略某些url*/
        String ingoreUrl = jwtProperties.getIgnoreUrl();
        String[] split = ingoreUrl.split(",");
        String servletPath = request.getServletPath();
        for (String path : split) {
            if (servletPath.contains(path)) {
                chain.doFilter(request, response);
                return;
            }
        }

        /*if (request.getServletPath().equals("/" + jwtProperties.getAuthPath())) {
        if (request.getServletPath().equals("/" + jwtProperties.getAuthPath())) {
            chain.doFilter(request, response);
            return;
        }*/

        /*拦截url:需要验证token*/
        // final String requestHeader = request.getHeader(jwtProperties.getHeader());

        String requestHeader = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJyYW5kb21LZXkiOiI3ZmpvaGsiLCJzdWIiOiJ1c2VyMSIsImV4cCI6MTU3OTI1MDM2NywiaWF0IjoxNTc4NjQ1NTY3fQ.XKll6TgeMyf9uecCKrFHTpAc0GxwLkh5GWvloyiQuvSWb3fggkCKFuLgTVkbtaEZLOpc-LIliR3GKu9PeeGj1w";

        String authToken = null;
        if (requestHeader != null && requestHeader.startsWith("Bearer ")) {

            authToken = requestHeader.substring(7);

            try {
                Object o =  redisTemplate.opsForValue().get(authToken);
                if (o == null) {
                    /*token过期*/
                    RenderUtil.renderJson(response, new ErrorTip(BizExceptionEnum.TOKEN_EXPIRED.getCode(), BizExceptionEnum.TOKEN_EXPIRED.getMessage()));
                    return;
                }
               //刷新并通过
                /*刷新token的时间*/
                //int tempTime = redisTemplate.getExpire(authToken).intValue();
                redisTemplate.expire(authToken, 86400, TimeUnit.SECONDS);

                /*通过token验证*/
                chain.doFilter(request, response);
                return;
            }catch (Exception e) {
                //有异常就是token解析失败
                /*服务器异常*/
                RenderUtil.renderJson(response, new ErrorTip(BizExceptionEnum.TOKEN_ERROR.getCode(), BizExceptionEnum.SYSTEM_ERROR.getMessage()));
                return;
            }
        }else {
            /*header没有带Bearer字段或者为null*/
            /*未登录*/
            RenderUtil.renderJson(response, new ErrorTip(BizExceptionEnum.TOKEN_ERROR.getCode(), BizExceptionEnum.TOKEN_WA.getMessage()));
            return;
        }
    }
}
