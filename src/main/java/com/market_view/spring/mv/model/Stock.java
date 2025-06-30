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

}
