package com.market_view.spring.mv.dto;

import java.math.BigDecimal;

public class LiborRateRequest {
    private BigDecimal normalRate;
    private BigDecimal specialRate;

    public BigDecimal getNormalRate() {
        return normalRate;
    }

    public void setNormalRate(BigDecimal normalRate) {
        this.normalRate = normalRate;
    }

    public BigDecimal getSpecialRate() {
        return specialRate;
    }

    public void setSpecialRate(BigDecimal specialRate) {
        this.specialRate = specialRate;
    }
}
