package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StockRepository extends MongoRepository<Stock, String> {
    boolean existsBySymbol(String symbol);
}