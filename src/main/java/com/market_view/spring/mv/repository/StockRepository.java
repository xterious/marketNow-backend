package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.Stock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends MongoRepository<Stock, String> {

    Page<Stock> findByMarketOriginAndStockSymbolContainingIgnoreCaseOrStockNameContainingIgnoreCase(
            String marketOrigin,
            String stockSymbolQuery,
            String stockNameQuery,
            Pageable pageable
    );
}
