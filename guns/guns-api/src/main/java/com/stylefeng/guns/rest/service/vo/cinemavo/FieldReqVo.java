package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;

/**
 * Request URL: http://115.29.141.32/cinema/getFields?cinemaId=1
 * Request Method: GET
 */
@Data
public class FieldReqVo implements Serializable {
    private static final long serialVersionUID = -6111649945260539344L;
    String imgPre = "http://img.meetingshop.cn";    // 写固定的
    String msg = "";
    String nowPage = "";
    String totalPage = "";
    Integer status;
    Object data;

    public static FieldReqVo ok(){
        FieldReqVo fieldReqVo = new FieldReqVo();
        fieldReqVo.setStatus(0);
        return fieldReqVo;
    }

    public static FieldReqVo ok(Object data){
        FieldReqVo fieldReqVo = FieldReqVo.ok();
        fieldReqVo.setData(data);
        return fieldReqVo;
    }
}
