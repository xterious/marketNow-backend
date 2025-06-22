package com.marketview.Spring.MV.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.marketview.Spring.MV.model.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
    Optional<Role> findByName(String name);
}
