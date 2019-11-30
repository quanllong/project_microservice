package com.stylefeng.guns.rest;
import lombok.Data;

@Data
public class BaseReqVo<T> {
    T data;
    String msg;
    int status;
    Integer nowPage;
    Integer totalPage;
    public static BaseReqVo ok(){
        BaseReqVo baseReqVo = new BaseReqVo();
        baseReqVo.setMsg("成功");
        baseReqVo.setStatus(0);
        return baseReqVo;
    }
    public static BaseReqVo ok(Object data){
        BaseReqVo baseReqVo = BaseReqVo.ok();
        baseReqVo.setData(data);
        return baseReqVo;
    }
    public static BaseReqVo fail(){
        BaseReqVo baseReqVo = new BaseReqVo();
        baseReqVo.setMsg("失败");
        baseReqVo.setStatus(500);
        return baseReqVo;
    }
    // quanllong
    public static BaseReqVo fail(Object msg){
        BaseReqVo baseReqVo = BaseReqVo.fail();
        baseReqVo.setMsg((String) msg);
        return baseReqVo;
    }
}
