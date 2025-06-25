package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.News;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsRepository extends MongoRepository<News, String> {
    List<News> findByCategory(String category);
}