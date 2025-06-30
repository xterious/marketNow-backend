package com.market_view.spring.mv.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "market")
@Data
public class Market {
    @Id
    private String id;

    private String name;
    private String description;

    // Constructors
    public Market() {}

    public Market(String name, String description) {
        this.name = name;
        this.description = description;
    }

}
