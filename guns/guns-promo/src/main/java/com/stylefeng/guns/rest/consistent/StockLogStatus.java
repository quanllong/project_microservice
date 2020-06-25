package com.stylefeng.guns.rest.consistent;

/**
 * @author quanlinglong
 */

public enum StockLogStatus {
    /**
     * 0初始值，1成功，2失败
     */
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
