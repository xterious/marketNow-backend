package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.News;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NewsRepository extends MongoRepository<News, String> {
}
