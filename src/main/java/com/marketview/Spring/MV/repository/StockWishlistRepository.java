package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.StockWishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockWishlistRepository extends MongoRepository<StockWishlist, String> {
    
    // Find wishlist by username
    Optional<StockWishlist> findByUsername(String username);
    
    // Check if a stock symbol exists in any wishlist
    @Query("{ 'favoriteStocks': ?0 }")
    List<StockWishlist> findByFavoriteStocksContaining(String stockSymbol);
    
    // Get all unique stock symbols across all wishlists
    @Query(value = "{}", fields = "{ 'favoriteStocks': 1 }")
    List<StockWishlist> findAllStockSymbols();
    
    // Count wishlists containing a specific stock
    @Query(value = "{ 'favoriteStocks': ?0 }", count = true)
    long countByFavoriteStocksContaining(String stockSymbol);
}