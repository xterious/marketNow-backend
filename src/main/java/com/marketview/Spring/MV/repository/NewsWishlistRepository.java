package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.NewsWishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface NewsWishlistRepository extends MongoRepository<NewsWishlist, String> {
    Optional<NewsWishlist> findByUsername(String username);
}