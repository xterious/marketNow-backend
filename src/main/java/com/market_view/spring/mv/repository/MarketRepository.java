package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.Market;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MarketRepository extends MongoRepository<Market, String> {
}
