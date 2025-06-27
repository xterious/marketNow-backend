package com.marketview.Spring.MV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.serializer.Serializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "stocks")
@Data
@NoArgsConstructor
public class Stock implements Serializable {
    @Id
    private String symbol;
    private String currency;
    private String description;
    private String displaySymbol;
    private String figi;
    private String mic;
    private String type;
    private String exchange;
    private Double currentPrice; // Maps to /quote's "c"
    private Double change; // Maps to /quote's "d"
    private Double percentChange; // Maps to /quote's "dp"
    private Double high; // Maps to /quote's "h"
    private Double low; // Maps to /quote's "l"
    private Double open; // Maps to /quote's "o"
    private Double previousClose; // Maps to /quote's "pc"
    private Long timestamp;

    public Stock(String symbol, String usd, String symbol1, String symbol2, Object o, Object o1, String commonStock, Double currentPrice, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        this.symbol = symbol;
        this.currency = usd;
        this.description = symbol1;
        this.displaySymbol = symbol2;
        this.figi = commonStock;
        this.currentPrice = currentPrice;
    }

    public Stock(String symbol, String currency, String description, String displaySymbol, Object figi, Object mic, String type, Double currentPrice, String exchange, Object change, Object percentChange, Object high, Object low, Object previousClose) {
        this.symbol = symbol;
        this.currency = currency;
        this.description = description;
        this.displaySymbol = displaySymbol;
        this.figi = type;
        this.currentPrice = currentPrice;
        this.exchange = exchange;
    }
}
