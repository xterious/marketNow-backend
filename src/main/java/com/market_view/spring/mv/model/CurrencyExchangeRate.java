package com.market_view.spring.mv.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class CurrencyExchangeRate {

    private String base;
    private String target;
    private BigDecimal rate;
    private BigDecimal finalRate;
    private String customerType;
    private long lastUpdated; // ✅ Add this

    public CurrencyExchangeRate(String base, String target, BigDecimal rate, BigDecimal finalRate, String customerType) {
        this.base = base;
        this.target = target;
        this.rate = rate.setScale(6, RoundingMode.HALF_UP);
        this.finalRate = finalRate.setScale(6, RoundingMode.HALF_UP);
        this.customerType = customerType.toLowerCase();
        this.lastUpdated = System.currentTimeMillis(); // default
    }

    // ✅ Add getter & setter for lastUpdated
    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    // Existing Getters & Setters...

    public String getBase() { return base; }

    public void setBase(String base) { this.base = base; }

    public String getTarget() { return target; }

    public void setTarget(String target) { this.target = target; }

    public BigDecimal getRate() { return rate; }

    public void setRate(BigDecimal rate) {
        this.rate = rate.setScale(6, RoundingMode.HALF_UP);
    }

    public BigDecimal getFinalRate() { return finalRate; }

    public void setFinalRate(BigDecimal finalRate) {
        this.finalRate = finalRate.setScale(6, RoundingMode.HALF_UP);
    }

    public String getCustomerType() { return customerType; }

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
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}
