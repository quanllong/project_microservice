package com.stylefeng.guns.rest.consistent;

public enum StockLogStatus {
    ORDER_INIT(0),
    ORDER_SUCCESS(1),
    ORDER_FAIL(2),
    ORDER_UNKNOWN(3);

    private Integer status;
    StockLogStatus(Integer status){
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
