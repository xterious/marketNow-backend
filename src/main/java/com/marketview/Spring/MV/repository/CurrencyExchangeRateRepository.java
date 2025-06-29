package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyExchangeRateRepository extends MongoRepository<CurrencyExchangeRate, String> {

    Optional<CurrencyExchangeRate> findById(String id);
    Optional<CurrencyExchangeRate> findByBaseAndTarget(String base, String target);
    List<CurrencyExchangeRate> findByBase(String base);
    List<CurrencyExchangeRate> findByTarget(String target);
    @Query("{ 'lastUpdated': { $gte: ?0 } }")
    List<CurrencyExchangeRate> findRecentlyUpdated(long timestamp);
    @Query("{ 'lastUpdated': { $lt: ?0 } }")
    List<CurrencyExchangeRate> findStaleRates(long timestamp);
}