package com.market_view.spring.mv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

@Document(collection = "stocks")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Stock implements Serializable {
    @Id
    private String stockSymbol;
    private String marketOrigin;
    private String stockName;
    private String type;

    public String getStockSymbol() {
        return stockSymbol;
    }
    public void setStockSymbol(String stockSymbol) {
        this.stockSymbol = stockSymbol;
    }
    public String getMarketOrigin() {
        return marketOrigin;
    }
    public void setMarketOrigin(String marketOrigin) {
        this.marketOrigin = marketOrigin;
    }
    public String getStockName() {
        return stockName;
    }
    public void setStockName(String stockName) {
        this.stockName = stockName;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
