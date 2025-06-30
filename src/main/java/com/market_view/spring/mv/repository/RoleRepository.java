package com.market_view.spring.mv.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.market_view.spring.mv.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}
