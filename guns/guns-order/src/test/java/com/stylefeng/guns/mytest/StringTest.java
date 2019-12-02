package com.stylefeng.guns.mytest;

import org.junit.Test;

import java.util.Arrays;

public class StringTest {

    @Test
    public void test1(){
        // quanllong
        String[] seatId = {"2","4","7"};
        int length = seatId.length;
        String seatIdStr = Arrays.toString(seatId);
        String substring = seatIdStr.substring(1, seatIdStr.length() - 1);
        String s = substring.replaceAll(", ", ",");
    }
}
