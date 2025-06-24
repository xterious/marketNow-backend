package com.marketview.Spring.MV.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "currency")
@Data
public class Currency {

    @Id
    private String id;

    private String code;           // e.g., USD, EUR
    private String name;           // e.g., US Dollar, Euro
    private double exchangeRate;   // exchange rate relative to some base currency

    // Default constructor
    public Currency() {}

    // Parameterized constructor
    public Currency(String code, String name, double exchangeRate) {
        this.code = code;
        this.name = name;
        this.exchangeRate = exchangeRate;
    }

}

