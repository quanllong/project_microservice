package com.stylefeng.guns.rest.modular.auth.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.core.exception.GunsException;
import com.stylefeng.guns.rest.common.exception.BizExceptionEnum;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthResponse;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.modular.auth.validator.IReqValidator;
import com.stylefeng.guns.rest.service.MtimeUserTService;
import com.stylefeng.guns.rest.service.vo.BaseResponseVO;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 请求验证的
 *
 * @author fengshuonan
 * @Date 2017/8/24 14:22
 */
@RestController
public class AuthController {

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Resource(name = "simpleValidator")
    private IReqValidator reqValidator;

    @Autowired
    private RedisTemplate redisTemplate;

    @Reference(interfaceClass = MtimeUserTService.class,check = false)
    MtimeUserTService mtimeUserTService;

    @RequestMapping(value = "${jwt.auth-path}")
//    public ResponseEntity<?> createAuthenticationToken(AuthRequest authRequest) {
    //        boolean validate = reqValidator.validate(authRequest);
    public BaseResponseVO createAuthenticationToken(AuthRequest authRequest) {


        boolean validate = false;

        /*校验用户信息:查询数据库*/
        String userName = authRequest.getUserName();
        String password = authRequest.getPassword();
        List<MtimeUserVO> list = mtimeUserTService.selectUserByName(userName);
        if (list != null && list.size() > 0) {
            MtimeUserVO mtimeUserVO = list.get(0);
            if (mtimeUserVO.getUserPwd().equals(password)) {
                validate = true;
            }
        }

        if (validate) {
            /*生成token*/
            final String randomKey = jwtTokenUtil.getRandomKey();
            final String token = jwtTokenUtil.generateToken(authRequest.getUserName(), randomKey);

            /*把用户信息返回回来*/
            MtimeUserVO mtimeUserVO = list.get(0);
            redisTemplate.opsForValue().set(token, mtimeUserVO);
            redisTemplate.expire(token, 600, TimeUnit.SECONDS);/*有效时间10min*/

            BaseResponseVO baseResponseVO = new BaseResponseVO();
            Map<String,Object> data = new HashMap<>();
            data.put("randomKey", randomKey);
            data.put("token", token);
            baseResponseVO.setData(data);
            baseResponseVO.setStatus(0);
            return baseResponseVO;

//            return ResponseEntity.ok(new AuthResponse(token, randomKey));
        } else {
            throw new GunsException(BizExceptionEnum.AUTH_REQUEST_ERROR);
        }
    }
}
