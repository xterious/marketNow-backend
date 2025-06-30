package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.CurrencyExchangeRate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class CurrencyExchangeRateRepositoryTest {

    @Autowired
    private CurrencyExchangeRateRepository repository;

    private CurrencyExchangeRate rate1, rate2, rate3;

    @BeforeEach
    public void setUp() {
        repository.deleteAll(); // Clean the collection before each test

        rate1 = new CurrencyExchangeRate("USD", "INR", new BigDecimal("83.456"), new BigDecimal("84.001"), "pro");
        rate1.setLastUpdated(System.currentTimeMillis());

        rate2 = new CurrencyExchangeRate("USD", "EUR", new BigDecimal("0.92"), new BigDecimal("0.94"), "normal");
        rate2.setLastUpdated(System.currentTimeMillis() - 100000); // stale

        rate3 = new CurrencyExchangeRate("EUR", "INR", new BigDecimal("90.123"), new BigDecimal("91.123"), "pro");
        rate3.setLastUpdated(System.currentTimeMillis());

        repository.saveAll(List.of(rate1, rate2, rate3));
    }

    @Test
    public void testFindByBaseAndTarget() {
        Optional<CurrencyExchangeRate> result = repository.findByBaseAndTarget("USD", "INR");
        assertTrue(result.isPresent());
        assertEquals("pro", result.get().getCustomerType());
    }

    @Test
    public void testFindByBaseAndTargetAndCustomerType() {
        Optional<CurrencyExchangeRate> result = repository.findByBaseAndTargetAndCustomerType("USD", "INR", "pro");
        assertTrue(result.isPresent());
        assertEquals("USD", result.get().getBase());
        assertEquals("INR", result.get().getTarget());
    }

    @Test
    public void testFindByBase() {
        List<CurrencyExchangeRate> usdRates = repository.findByBase("USD");
        assertEquals(2, usdRates.size());
    }

    @Test
    public void testFindByTarget() {
        List<CurrencyExchangeRate> inrRates = repository.findByTarget("INR");
        assertEquals(2, inrRates.size());
    }

    @Test
    public void testFindRecentlyUpdated() {
        long cutoff = System.currentTimeMillis() - 5000;
        List<CurrencyExchangeRate> recent = repository.findRecentlyUpdated(cutoff);
        assertEquals(2, recent.size());
    }

    @Test
    public void testFindStaleRates() {
        long cutoff = System.currentTimeMillis() - 5000;
        List<CurrencyExchangeRate> stale = repository.findStaleRates(cutoff);
        assertEquals(1, stale.size());
        assertEquals("EUR", stale.get(0).getTarget());
    }
}
