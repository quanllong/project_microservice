package com.stylefeng.guns.rest.consistant;

// quanllong
public enum OrderStatus {
    // 0-待支付,1-已支付,2-已关闭
    NOT_PAY(0,"待支付"),
    PAY_SUCCESS(1,"已支付"),
    CLOSED(2,"已关闭");

    private Integer code;
    private String status;

    OrderStatus() {
    }

    OrderStatus(Integer code, String status) {
        this.code = code;
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
