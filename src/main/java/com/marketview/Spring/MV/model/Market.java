package com.marketview.Spring.MV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "market")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Market {
    @Id
    private String id;

    private String name;
    private String description;



}
