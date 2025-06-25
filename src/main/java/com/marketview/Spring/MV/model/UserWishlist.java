package com.marketview.Spring.MV.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Set;

@Document(collection = "userWishlists")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWishlist implements Serializable {

    @Id
    private String username;
    private Set<String> favoriteStocks;
    private Set<String> favoriteCurrencies;
    private Set<String> favoriteNews;

}