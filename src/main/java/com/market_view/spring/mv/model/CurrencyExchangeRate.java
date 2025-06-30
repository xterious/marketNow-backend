package com.marketview.Spring.MV.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Data
@Document(collection = "currencyExchangeRate")
public class CurrencyExchangeRate {

    @Indexed(unique = true)
    private String id; // Unique identifier: base-target-customerType
    private String base;
    private String target;
    private BigDecimal rate; // Base rate from API
    private BigDecimal finalRate; // Rate after LIBOR adjustment
    private String customerType;
    private long lastUpdated; // Timestamp for when the rate was last updated

    public CurrencyExchangeRate(String base, String target, BigDecimal rate, BigDecimal finalRate, String customerType) {
        this.base = base.toUpperCase();
        this.target = target.toUpperCase();
        this.rate = rate.setScale(6, RoundingMode.HALF_UP);
        this.finalRate = finalRate.setScale(6, RoundingMode.HALF_UP);
        this.customerType = customerType.toLowerCase();
        this.id = generateId(this.base, this.target, this.customerType);
        this.lastUpdated = Instant.now().toEpochMilli();
    }

    private static String generateId(String base, String target, String customerType) {
        return String.format("%s-%s-%s", base.toUpperCase(), target.toUpperCase(), customerType.toLowerCase());
    }

    @Override
    public String toString() {
        return "CurrencyExchangeRate{" +
                "id='" + id + '\'' +
                ", base='" + base + '\'' +
                ", target='" + target + '\'' +
                ", rate=" + rate +
                ", finalRate=" + finalRate +
                ", customerType='" + customerType + '\'' +
                ", lastUpdated=" + lastUpdated +
                '}';
    }
}