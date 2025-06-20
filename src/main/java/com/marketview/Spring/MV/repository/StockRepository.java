package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface StockRepository extends MongoRepository<Stock, String> {
    Optional<Stock> findBySymbol(String symbol);
}
