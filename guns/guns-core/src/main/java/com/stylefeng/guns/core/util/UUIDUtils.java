package com.stylefeng.guns.core.util;

import java.util.UUID;

/**
 * @Author:ys
 * @Date:2019/12/1
 * @time:20:43
 */
public class UUIDUtils {
    /**
     * 这是一个获取随机UUID的公共类
     * @return
     */
    public static String getUuid(){
        // 取18位，quanllong
        return UUID.randomUUID().toString().replace("-","").substring(0,18);
    }
}
