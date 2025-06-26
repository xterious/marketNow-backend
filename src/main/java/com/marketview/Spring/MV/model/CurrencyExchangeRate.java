package com.marketview.Spring.MV.model;


import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
public class CurrencyExchangeRate implements Serializable {
    private String base;
    private String target;
    private BigDecimal rate;
    private BigDecimal finalRate;
    private String customerType;

    public CurrencyExchangeRate(String base, String target, BigDecimal rate, String customerType) {
        this.base = base;
        this.target = target;
        this.rate = rate;
        this.customerType = customerType;
        this.finalRate = calculateFinalRate(rate, customerType);
    }

    private BigDecimal calculateFinalRate(BigDecimal baseRate, String customerType) {
        BigDecimal liborRate = "special".equalsIgnoreCase(customerType) ? BigDecimal.valueOf(0.005) : BigDecimal.valueOf(0.01);
        return baseRate.add(baseRate.multiply(liborRate)).setScale(4, BigDecimal.ROUND_HALF_UP);
    }
}