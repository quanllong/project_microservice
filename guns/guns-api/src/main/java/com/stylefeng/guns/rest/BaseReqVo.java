package com.stylefeng.guns.rest;
import lombok.Data;
import java.io.Serializable;

@Data
public class BaseReqVo<T> implements Serializable {
    private static final long serialVersionUID = 8019703345977793521L;
    Object data;    // 这里不用泛型了，用Object。quanllong
    String msg;
    Integer nowPage;
    Integer totalPage;
    Integer status;
    String imgPre;

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

    public static BaseReqVo ok(Object data,String imgPre){
        BaseReqVo baseReqVo = BaseReqVo.ok();
        baseReqVo.setData(data);
        baseReqVo.setImgPre(imgPre);
        return baseReqVo;
    }
}
