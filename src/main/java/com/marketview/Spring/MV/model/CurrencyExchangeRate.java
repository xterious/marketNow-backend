package com.marketview.Spring.MV.model;

import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class CurrencyExchangeRate {

    private String base;
    private String target;
    private BigDecimal rate; // Base rate from API
    private BigDecimal finalRate; // Rate after LIBOR adjustment
    private String customerType;

    public CurrencyExchangeRate(String base, String target, BigDecimal rate, BigDecimal finalRate, String customerType) {
        this.base = base;
        this.target = target;
        this.rate = rate.setScale(6, RoundingMode.HALF_UP); // Use RoundingMode instead of a deprecated constant
        this.finalRate = finalRate.setScale(6, RoundingMode.HALF_UP); // Use RoundingMode
        this.customerType = customerType.toLowerCase();
    }
    @Override
    public String toString() {
        return "CurrencyExchangeRate{" +
                "base='" + base + '\'' +
                ", target='" + target + '\'' +
                ", rate=" + rate +
                ", finalRate=" + finalRate +
                ", customerType='" + customerType + '\'' +
                '}';
    }
}