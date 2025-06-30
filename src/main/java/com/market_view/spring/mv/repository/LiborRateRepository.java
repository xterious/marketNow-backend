package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.LiborRate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiborRateRepository extends MongoRepository<LiborRate, String> {
}
