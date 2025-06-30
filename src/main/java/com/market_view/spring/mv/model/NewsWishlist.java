package com.market_view.spring.mv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Document(collection = "newsWishlists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewsWishlist {
    @Id
    private String username;
    private Set<String> favoriteNews = new HashSet<>();

    public NewsWishlist(String username) {
        this.username = username;
    }
}