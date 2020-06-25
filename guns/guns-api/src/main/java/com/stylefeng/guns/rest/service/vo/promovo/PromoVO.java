package com.stylefeng.guns.rest.service.vo.promovo;

import lombok.Data;


import java.io.Serializable;
import java.util.List;
@Data
public class PromoVO implements Serializable {

    private static final long serialVersionUID = 540219948136153854L;
    /**
     * data : [{"cinemaAddress":"北京市顺义区华联金街购物中心","cinemaId":1,"cinemaName":"大地影院(顺义店)","description":"大地影院周年庆，等你来兑换","endTime":"2019-09-19 15:35:18","imgAddress":"cinema6.jpg","price":10,"startTime":"2019-08-15 15:35:12","status":1,"stock":9196,"uuid":1},{"cinemaAddress":"北京市中关村海龙大厦","cinemaId":2,"cinemaName":"大地影院(中关村店)","description":"大地影院酬宾活动","endTime":"2019-08-29 15:36:30","imgAddress":"cinema6.jpg","price":15,"startTime":"2019-08-01 15:36:23","status":1,"stock":0,"uuid":2},{"cinemaAddress":"北京市朝阳区大屯路50号金街商场","cinemaId":3,"cinemaName":"万达影院(大屯店)","description":"万达大酬宾","endTime":"2019-08-30 15:37:03","imgAddress":"cinema2.jpg","price":20,"startTime":"2019-08-01 15:36:54","status":1,"stock":0,"uuid":3}]
     * imgPre :
     * msg :
     * nowPage :
     * status : 0
     * totalPage :
     */

    private String imgPre;
    private String msg;
    private String nowPage;
    private int status;
    private String totalPage;
    private List<PromoData> data;
}
