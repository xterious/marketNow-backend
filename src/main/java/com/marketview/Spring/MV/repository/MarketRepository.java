package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.Market;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MarketRepository extends MongoRepository<Market, String> {
}
