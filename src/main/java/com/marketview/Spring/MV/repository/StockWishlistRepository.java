package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.StockWishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StockWishlistRepository extends MongoRepository<StockWishlist, String> {
    Optional<StockWishlist> findByUsername(String username);
}