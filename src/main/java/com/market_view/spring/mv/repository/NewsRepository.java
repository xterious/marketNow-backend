package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.News;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NewsRepository extends MongoRepository<News, String> {
    
    // Find by category
    List<News> findByCategory(String category);
    
    // Find by headline
    Optional<News> findByHeadline(String headline);
    
    // Find by URL
    Optional<News> findByUrl(String url);
    
    // Find news older than specified timestamp
    List<News> findByDatetimeLessThan(long timestamp);
    
    // Find news newer than specified timestamp
    List<News> findByDatetimeGreaterThan(long timestamp);
    
    // Find news between timestamps
    List<News> findByDatetimeBetween(long startTime, long endTime);
    
    // Find recent news (sorted by datetime descending)
    @Query(value = "{}", sort = "{ 'datetime': -1 }")
    List<News> findRecentNews();
    
    // Find top headlines (limit results)
    @Query(value = "{}", sort = "{ 'datetime': -1 }")
    List<News> findTopHeadlines();
    
    // Find news by source
    List<News> findBySource(String source);
    
    // Custom query to find news by multiple identifiers (for wishlist checking)
    @Query("{ $or: [ { '_id': ?0 }, { 'url': ?0 }, { 'headline': ?0 } ] }")
    Optional<News> findByAnyIdentifier(String identifier);
}