package com.stylefeng.guns.rest.service.vo.cinemavo;

import lombok.Data;

import java.io.Serializable;

/**
 * Request URL: http://115.29.141.32/cinema/getFields?cinemaId=1
 * Request Method: GET
 *
 */
@Data
public class FieldReqVO4Field implements Serializable {
    private static final long serialVersionUID = -6111649945260539344L;
    String imgPre = "http://img.meetingshop.cn";    // 写固定的，quanllong
    String msg = "";
    String nowPage = "";
    String totalPage = "";
    Integer status;
    Object data;

    public static FieldReqVO4Field ok(){
        FieldReqVO4Field fieldReqVo4Field = new FieldReqVO4Field();
        fieldReqVo4Field.setStatus(0);
        return fieldReqVo4Field;
    }

    public static FieldReqVO4Field ok(Object data){
        FieldReqVO4Field fieldReqVo4Field = FieldReqVO4Field.ok();
        fieldReqVo4Field.setData(data);
        return fieldReqVo4Field;
    }
}
