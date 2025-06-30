package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.CurrencyWishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyWishlistRepository extends MongoRepository<CurrencyWishlist, String> {
    
    // Find wishlist by username
    Optional<CurrencyWishlist> findByUsername(String username);
    
    // Check if a currency code exists in any wishlist
    @Query("{ 'favoriteCurrencies': ?0 }")
    List<CurrencyWishlist> findByFavoriteCurrenciesContaining(String currencyCode);
    
    // Get all unique currency codes across all wishlists
    @Query(value = "{}", fields = "{ 'favoriteCurrencies': 1 }")
    List<CurrencyWishlist> findAllCurrencyCodes();
    
    // Count wishlists containing a specific currency
    @Query(value = "{ 'favoriteCurrencies': ?0 }", count = true)
    long countByFavoriteCurrenciesContaining(String currencyCode);
}