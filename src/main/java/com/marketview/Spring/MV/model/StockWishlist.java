package com.marketview.Spring.MV.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "stockWishlists")
@Data
public class StockWishlist {
    @Id
    private String username;
    private Set<String> favoriteStocks = new HashSet<>();
    public StockWishlist(String username) {
        this.username = username;
    }
}