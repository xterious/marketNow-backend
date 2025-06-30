package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends MongoRepository<Stock, String> {
    
    // Find by symbol (should match the field name in Stock)
    Optional<Stock> findByStockSymbol(String stockSymbol);
    
    // Find by marketOrigin (used for exchange/currency)
    List<Stock> findByMarketOrigin(String marketOrigin);
    
    // Find by type
    List<Stock> findByType(String type);
    
    // Find stocks with price greater than
    @Query("{ 'currentPrice': { $gt: ?0 } }")
    List<Stock> findByCurrentPriceGreaterThan(Double price);
    
    // Find stocks with price less than
    @Query("{ 'currentPrice': { $lt: ?0 } }")
    List<Stock> findByCurrentPriceLessThan(Double price);
    
    // Find stocks updated after timestamp
    @Query("{ 'lastUpdated': { $gte: ?0 } }")
    List<Stock> findRecentlyUpdated(long timestamp);
    
    // Find stocks that need updating
    @Query("{ 'lastUpdated': { $lt: ?0 } }")
    List<Stock> findStaleStocks(long timestamp);
}