package com.marketview.Spring.MV.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyExchangeRate {

    private String base;
    private String target;
    private BigDecimal rate; // Base rate from API
    private BigDecimal finalRate; // Rate after LIBOR adjustment
    private String customerType;

    public CurrencyExchangeRate(String base, String target, BigDecimal rate, BigDecimal finalRate, String customerType) {
        this.base = base;
        this.target = target;
        this.rate = rate.setScale(6, RoundingMode.HALF_UP); // Use RoundingMode instead of deprecated constant
        this.finalRate = finalRate.setScale(6, RoundingMode.HALF_UP); // Use RoundingMode
        this.customerType = customerType.toLowerCase();
    }

    // Getters and Setters
    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public BigDecimal getRate() {
        return rate;
    }

    public void setRate(BigDecimal rate) {
        this.rate = rate.setScale(6, RoundingMode.HALF_UP); // Apply scaling with RoundingMode
    }

    public BigDecimal getFinalRate() {
        return finalRate;
    }

    public void setFinalRate(BigDecimal finalRate) {
        this.finalRate = finalRate.setScale(6, RoundingMode.HALF_UP); // Apply scaling with RoundingMode
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
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