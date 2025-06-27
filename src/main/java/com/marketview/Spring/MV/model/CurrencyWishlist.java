package com.marketview.Spring.MV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "currencyWishlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyWishlist {
    @Id
    private String username;
    private Set<String> favoriteCurrencies = new HashSet<>();


    public CurrencyWishlist(String username) {
        this.username = username;
    }
}