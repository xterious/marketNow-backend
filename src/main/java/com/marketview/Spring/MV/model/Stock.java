package com.marketview.Spring.MV.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stocks")
@Data
public class Stock {
    @Id
    private String symbol;
    private String currency;
    private String description;
    private String displaySymbol;
    private String figi;
    private String mic;
    private String type;
    private Double currentPrice; // Maps to /quote's "c"
    private Double change; // Maps to /quote's "d"
    private Double percentChange; // Maps to /quote's "dp"
    private Double high; // Maps to /quote's "h"
    private Double low; // Maps to /quote's "l"
    private Double open; // Maps to /quote's "o"
    private Double previousClose; // Maps to /quote's "pc"
    private Long timestamp;

    public Stock() {}

    public Stock(String symbol, String currency, String description, String displaySymbol,
                 String figi, String mic, String type, Double currentPrice,
                 Double change, Double percentChange, Double high, Double low,
                 Double open, Double previousClose) {
        this.symbol = symbol;
        this.currency = currency;
        this.description = description;
        this.displaySymbol = displaySymbol;
        this.figi = figi;
        this.mic = mic;
        this.type = type;
        this.currentPrice = currentPrice;
        this.change = change;
        this.percentChange = percentChange;
        this.high = high;
        this.low = low;
        this.open = open;
        this.previousClose = previousClose;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getDisplaySymbol() { return displaySymbol; }
    public void setDisplaySymbol(String displaySymbol) { this.displaySymbol = displaySymbol; }
    public String getFigi() { return figi; }
    public void setFigi(String figi) { this.figi = figi; }
    public String getMic() { return mic; }
    public void setMic(String mic) { this.mic = mic; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Double getCurrentPrice() { return currentPrice; }
    public void setCurrentPrice(Double currentPrice) { this.currentPrice = currentPrice; }
    public Double getChange() { return change; }
    public void setChange(Double change) { this.change = change; }
    public Double getPercentChange() { return percentChange; }
    public void setPercentChange(Double percentChange) { this.percentChange = percentChange; }
    public Double getHigh() { return high; }
    public void setHigh(Double high) { this.high = high; }
    public Double getLow() { return low; }
    public void setLow(Double low) { this.low = low; }
    public Double getOpen() { return open; }
    public void setOpen(Double open) { this.open = open; }
    public Double getPreviousClose() { return previousClose; }
    public void setPreviousClose(Double previousClose) { this.previousClose = previousClose; }
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}