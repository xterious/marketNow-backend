package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.UserWishlist;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserWishlistRepository extends MongoRepository<UserWishlist, String> {

    /**
     * Find a user's wishlist by username.
     * @param username The username of the user
     * @return An Optional containing the UserWishlist if found, empty otherwise
     */
    Optional<UserWishlist> findByUsername(String username);

    /**
     * Delete a user's wishlist by username.
     * @param username The username of the user
     */
    void deleteByUsername(String username);
}