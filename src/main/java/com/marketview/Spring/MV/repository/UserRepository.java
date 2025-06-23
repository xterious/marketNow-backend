package com.marketview.Spring.MV.repository;


import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.marketview.Spring.MV.model.User;

public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByProviderAndProviderId(String provider, String providerId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}

