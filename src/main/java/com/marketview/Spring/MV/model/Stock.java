package com.marketview.Spring.MV.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "stocks")
@Data
public class Stock {

    @Id
    private String id;
    private String symbol;
    private double price;
    private long volume;

}

