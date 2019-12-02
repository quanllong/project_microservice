package com.stylefeng.guns.rest.service.vo.filmvo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author:ys
 * @Date:2019/11/28
 * @time:21:04
 */
@Data
public class BannersVO implements Serializable {

    private static final long serialVersionUID = -4776183000543778987L;

    private String bannerId;

    private String bannerAddress;

    private String bannerUrl;
}
