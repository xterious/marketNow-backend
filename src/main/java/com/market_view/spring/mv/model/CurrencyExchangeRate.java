package com.market_view.spring.mv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "currencyExchangeRate")
public class CurrencyExchangeRate {

    @Indexed(unique = true)
    private String id; // e.g. "EUR-USD-retail"
    private String base;
    private String target;
    private BigDecimal rate;
    private BigDecimal finalRate;
    private String customerType;
    private long lastUpdated;

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
        return String.format("%s-%s-%s",
                base.toUpperCase(),
                target.toUpperCase(),
                customerType.toLowerCase()
        );
    }
}