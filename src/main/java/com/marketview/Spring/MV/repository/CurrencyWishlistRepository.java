package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.CurrencyWishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CurrencyWishlistRepository extends MongoRepository<CurrencyWishlist, String> {
    Optional<CurrencyWishlist> findByUsername(String username);
}