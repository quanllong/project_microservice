package com.stylefeng.guns.rest.modular.promo.bean;

import lombok.Data;

@Data
public class Stock {
    public Stock() {
    }

    public Stock(Integer promoId, Integer amount) {
        this.promoId = promoId;
        this.amount = amount;
    }

    Integer promoId;
    Integer amount;
}
