package com.marketview.Spring.MV.repository;

import com.marketview.Spring.MV.model.Currency;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.Optional;

public interface CurrencyRepository extends MongoRepository<Currency, String> {
    Optional<Currency> findByCode(String code);
}
