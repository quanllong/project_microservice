package com.stylefeng.guns.rest.common.exception;

import com.stylefeng.guns.core.exception.ServiceExceptionEnum;

/**
 * 所有业务异常的枚举
 *
 * @author fengshuonan
 * @date 2016年11月12日 下午5:04:51
 */
public enum BizExceptionEnum implements ServiceExceptionEnum {

    /**
     * token异常
     */
    // TOKEN_EXPIRED(700, "token过期"),
    TOKEN_EXPIRED(700, "登录已失效，请重新登录"),   //quanllong
    TOKEN_ERROR(700, "token验证失败"),
    TOKEN_WA(700, "你还未登录，请先登录"),
    /**
     * 签名异常
     */
    SIGN_ERROR(700, "签名验证失败"),

    /**
     * 其他
     */
    AUTH_REQUEST_ERROR(400, "账号密码错误"),
    SYSTEM_ERROR(1,"服务器异常");



    BizExceptionEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    private Integer code;

    private String message;

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
