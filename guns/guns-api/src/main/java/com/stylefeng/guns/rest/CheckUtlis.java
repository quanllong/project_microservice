package com.stylefeng.guns.rest;


public class CheckUtlis {

    public static Boolean checkStr(String str) {
        String trim = str.trim();
        if (trim.indexOf(" ") != -1) {
            return false;
        }
        return true;
    }


    public static Boolean checkAddress(String address) {
        /**
         * 匹配汉字
         */
        boolean matches = address.matches("^[\\u4e00-\\u9fa5]{0,}$");
        return matches;
    }
}
