package com.stylefeng.guns.rest.service.vo;

import lombok.Data;
import java.io.Serializable;

@Data
public class BaseResponseVO<T> implements Serializable {
    private static final long serialVersionUID = 1354563245941653497L;//这里是idea随机生成的

    private T data;

    private String msg;

    private int status;

    private static final String REGISTERERROR = "用户已存在";

    private static final String  SYSTEMERROR= "系统异常";

    public BaseResponseVO() {
    }

    public BaseResponseVO(T data, String msg, int status) {
        this.data = data;
        this.msg = msg;
        this.status = status;
    }

    public static BaseResponseVO ok() {
        BaseResponseVO<Object> objectBaseResponseVO = new BaseResponseVO<>();
        objectBaseResponseVO.setMsg(null);

        objectBaseResponseVO.setStatus(0);
        return objectBaseResponseVO;
    }

    public static BaseResponseVO ok(String msg) {
        BaseResponseVO<Object> objectBaseResponseVO = new BaseResponseVO<>();
        objectBaseResponseVO.setMsg(msg);
        objectBaseResponseVO.setStatus(0);
        return objectBaseResponseVO;
    }

    public static BaseResponseVO fail(String msg) {
        BaseResponseVO<Object> objectBaseResponseVO = new BaseResponseVO<>();
        objectBaseResponseVO.setMsg(msg);
        objectBaseResponseVO.setStatus(1);
        return objectBaseResponseVO;
    }

    /**
     * 注册失败:用户已存在
     * @param msg
     * @return
     */
    public static BaseResponseVO registerFail() {
        BaseResponseVO<Object> objectBaseResponseVO = new BaseResponseVO<>();
        objectBaseResponseVO.setMsg(REGISTERERROR);
        objectBaseResponseVO.setStatus(1);
        return objectBaseResponseVO;
    }

    /**
     * 系统异常
     * @return
     */
    public static BaseResponseVO systemError() {
        BaseResponseVO<Object> objectBaseResponseVO = new BaseResponseVO<>();
        objectBaseResponseVO.setMsg(SYSTEMERROR);
        objectBaseResponseVO.setStatus(1);
        return objectBaseResponseVO;
    }

    /**
     * 系统异常
     * @param msg
     * @return
     */
    public static BaseResponseVO systemError(String msg) {
        BaseResponseVO<Object> objectBaseResponseVO = new BaseResponseVO<>();
        objectBaseResponseVO.setMsg(msg);
        objectBaseResponseVO.setStatus(999);
        return objectBaseResponseVO;
    }

    public static BaseResponseVO systemFail(String msg) {
        BaseResponseVO<Object> objectBaseResponseVO = new BaseResponseVO<>();
        objectBaseResponseVO.setMsg(msg);
        objectBaseResponseVO.setStatus(1);
        return objectBaseResponseVO;
    }

}
