package com.marketview.Spring.MV.repository;
import com.marketview.Spring.MV.model.News;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NewsRepository extends MongoRepository<News, String> {
    List<News> findByCategory(String category);
    List<News> findByRelatedStock(String relatedStock);
}
