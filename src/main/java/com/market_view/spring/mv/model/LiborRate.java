package com.market_view.spring.mv.model;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "liborRate")
public class LiborRate {

    private String id = "LIBOR"; // always only one document

    private BigDecimal normalRate;
    private BigDecimal specialRate;

    public LiborRate() {
        this.normalRate = BigDecimal.valueOf(0.02);
        this.specialRate = BigDecimal.valueOf(0.014);
    }
}
