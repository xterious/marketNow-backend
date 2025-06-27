package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CurrencyExchangeRateRepository extends MongoRepository<CurrencyExchangeRate, String> {

    // Find by base and target currency combination
    Optional<CurrencyExchangeRate> findByBaseAndTarget(String base, String target);

    // Find by base and target currency with a customer type
    Optional<CurrencyExchangeRate> findByBaseAndTargetAndCustomerType(
            String base, String target, String customerType);

    // Find all rates for a specific base currency
    List<CurrencyExchangeRate> findByBase(String base);

    // Find all rates for a specific target currency
    List<CurrencyExchangeRate> findByTarget(String target);

    // Find rates updated after a specific timestamp
    @Query("{ 'lastUpdated': { $gte: ?0 } }")
    List<CurrencyExchangeRate> findRecentlyUpdated(long timestamp);

    // Find rates that need updating (older than specified time)
    @Query("{ 'lastUpdated': { $lt: ?0 } }")
    List<CurrencyExchangeRate> findStaleRates(long timestamp);
}
