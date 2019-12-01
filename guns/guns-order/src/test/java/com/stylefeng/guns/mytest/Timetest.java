package com.stylefeng.guns.mytest;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Timetest {

    @Test
    public void test1(){
        // 测试能否生成订单的时间戳 quanlong
        Date date = new Date();
        System.out.println(date);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String s = simpleDateFormat.format(new Date());
        try {
            Date parse = simpleDateFormat.parse(s);
            System.out.println(parse);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        new Date().getTime();
        long l = System.currentTimeMillis();

    }
}
