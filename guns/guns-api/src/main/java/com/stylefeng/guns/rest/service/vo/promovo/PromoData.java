package com.stylefeng.guns.rest.service.vo.promovo;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
public class PromoData implements Serializable {

        private static final long serialVersionUID = 6086688078639988207L;
        /**
         * cinemaAddress : 北京市顺义区华联金街购物中心
         * cinemaId : 1
         * cinemaName : 大地影院(顺义店)
         * description : 大地影院周年庆，等你来兑换
         * endTime : 2019-09-19 15:35:18
         * imgAddress : cinema6.jpg
         * price : 10
         * startTime : 2019-08-15 15:35:12
         * status : 1
         * stock : 9196
         * uuid : 1
         */

        private String cinemaAddress;
        private int cinemaId;
        private String cinemaName;
        private String description;
        private Date endTime;
        private String imgAddress;
        private BigDecimal price;
        private Date startTime;
        private int status;
        private int stock;
        private int uuid;

}
