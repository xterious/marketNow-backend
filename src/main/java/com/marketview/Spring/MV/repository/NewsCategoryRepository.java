package com.marketview.Spring.MV.repository;


import com.marketview.Spring.MV.model.NewsCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface NewsCategoryRepository extends MongoRepository<NewsCategory, String> {
    Optional<NewsCategory> findByCategory(String category);
    List<NewsCategory> findAll();
}
