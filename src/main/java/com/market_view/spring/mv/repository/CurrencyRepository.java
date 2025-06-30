package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.Currency;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CurrencyRepository extends MongoRepository<Currency, String> {
    // You can add custom query methods here if needed
}
