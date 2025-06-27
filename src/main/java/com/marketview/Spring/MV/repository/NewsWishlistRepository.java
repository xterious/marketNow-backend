package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.NewsWishlist;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface NewsWishlistRepository extends MongoRepository<NewsWishlist, String> {
    
    // Find wishlist by username
    Optional<NewsWishlist> findByUsername(String username);
    
    // Check if a news item exists in any wishlist
    @Query("{ 'favoriteNews': ?0 }")
    List<NewsWishlist> findByFavoriteNewsContaining(String newsIdentifier);
    
    // Get all unique news identifiers across all wishlists
    @Query(value = "{}", fields = "{ 'favoriteNews': 1 }")
    List<NewsWishlist> findAllNewsIdentifiers();
    
    // Count wishlists containing a specific news item
    @Query(value = "{ 'favoriteNews': ?0 }", count = true)
    long countByFavoriteNewsContaining(String newsIdentifier);
    
    // Check if any news item from a list exists in wishlists
    @Query("{ 'favoriteNews': { $in: ?0 } }")
    List<NewsWishlist> findByFavoriteNewsContainingAny(Set<String> newsIdentifiers);
    
    // Get all wishlisted news items (flattened)
    @Query(value = "{}", fields = "{ 'favoriteNews': 1, '_id': 0 }")
    List<NewsWishlist> findAllFavoriteNews();
}