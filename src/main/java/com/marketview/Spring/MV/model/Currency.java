package com.marketview.Spring.MV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "currency")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Currency {

    @Id
    private String id;

    private String code;           // e.g., USD, EUR
    private String name;           // e.g., US Dollar, Euro
    private double exchangeRate;   // exchange rate relative to some base currency


}

