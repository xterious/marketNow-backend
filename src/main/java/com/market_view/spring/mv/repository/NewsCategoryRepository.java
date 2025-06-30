package com.market_view.spring.mv.repository;


import com.market_view.spring.mv.model.NewsCategory;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;


public interface NewsCategoryRepository extends MongoRepository<NewsCategory, String> {
    Optional<NewsCategory> findByCategory(String category);
    List<NewsCategory> findAll();
}