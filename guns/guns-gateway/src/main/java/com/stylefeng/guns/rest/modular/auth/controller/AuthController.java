package com.stylefeng.guns.rest.modular.auth.controller;


import com.alibaba.dubbo.config.annotation.Reference;
import com.stylefeng.guns.rest.modular.auth.controller.dto.AuthRequest;
import com.stylefeng.guns.rest.modular.auth.util.JwtTokenUtil;
import com.stylefeng.guns.rest.modular.auth.validator.IReqValidator;
import com.stylefeng.guns.rest.service.MtimeUserTService;
import com.stylefeng.guns.rest.service.vo.BaseResponseVO;
import com.stylefeng.guns.rest.service.vo.MtimeUserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Autowired
    MtimeUserTService mtimeUserTService;

    @RequestMapping(value = "${jwt.auth-path}")
//    public ResponseEntity<?> createAuthenticationToken(AuthRequest authRequest) {
    //        boolean validate = reqValidator.validate(authRequest);
    public BaseResponseVO createAuthenticationToken(AuthRequest authRequest) {

        try {
            //测试异常
            //int i = 1/0;

            String userName = authRequest.getUserName();
            String password = authRequest.getPassword();

            /*校验用户信息:查询数据库*/
            boolean validate = mtimeUserTService.login(userName,password);
            if (validate) {
                /*生成token*/
                final String randomKey = jwtTokenUtil.getRandomKey();
                final String token = jwtTokenUtil.generateToken(authRequest.getUserName(), randomKey);

                /*把用户信息返回回来*/
                List<MtimeUserVO> list = mtimeUserTService.selectUserAndPwd(userName,password);
                MtimeUserVO mtimeUserVO = list.get(0);
                redisTemplate.opsForValue().set(token, mtimeUserVO);
                redisTemplate.expire(token, 24, TimeUnit.HOURS);/*有效时间是1天*/

                BaseResponseVO baseResponseVO = new BaseResponseVO();
                Map<String,Object> data = new HashMap<>();
                data.put("randomKey", randomKey);
                data.put("token", token);
                baseResponseVO.setData(data);
                baseResponseVO.setStatus(0);
                return baseResponseVO;

//            return ResponseEntity.ok(new AuthResponse(token, randomKey));
            }
            else {
                return BaseResponseVO.fail("用户名或密码错误");
                //throw new GunsException(BizExceptionEnum.AUTH_REQUEST_ERROR);
            }
        }catch (Exception e) {
            return BaseResponseVO.systemFail("系统出现异常，请联系管理员");
        }
    }
}
