package com.stylefeng.guns.core.exception;

/**
 * Guns异常枚举
 *
 * @author fengshuonan
 * @Date 2017/12/28 下午10:33
 */
public enum GunsExceptionEnum implements ServiceExceptionEnum {

    /**
     * 其他
     */
    INVLIDE_DATE_STRING(400, "输入日期格式不对"),

    /**
     * 其他
     */
    WRITE_ERROR(500, "渲染界面错误"),

    /**
     * 文件上传
     */
    FILE_READING_ERROR(400, "FILE_READING_ERROR!"),
    FILE_NOT_FOUND(400, "FILE_NOT_FOUND!"),

    /**
     * 错误的请求
     */
    REQUEST_NULL(400, "请求有错误"),
    SERVER_ERROR(500, "服务器异常"),

    /**
     * 事务相关
     * quanllong
     */
    DATABASE_ERROE(999,"订单入库失败"),
    REDIS_ERROR(999,"扣减redis缓存失败"),
    STOCK_LOG_ERROR(999,"创建库存流水失败"),
    CREATE_ORDER_ERROR(999,"创建订单失败");


    GunsExceptionEnum(int code, String message) {
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
