package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.LiborRate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LiborRateRepository extends MongoRepository<LiborRate, String> {
}
