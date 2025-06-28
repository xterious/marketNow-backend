package com.marketview.Spring.MV.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import com.marketview.Spring.MV.repository.CurrencyExchangeRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;
import java.util.Set;

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "EUR", "GBP", "JPY", "CAD","INR"); // Add supported currencies

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CurrencyExchangeRateRepository currencyExchangeRateRepository;

    @Value("${fixer.api.key}")
    private String fixerApiKey;

    private BigDecimal liborSpreadNormal = new BigDecimal("0.005"); // Default 0.5%
    private BigDecimal liborSpreadSpecial = new BigDecimal("0.002"); // Default 0.2%

    public CurrencyService(RestTemplate restTemplate, ObjectMapper objectMapper,
                           CurrencyExchangeRateRepository currencyExchangeRateRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
    }

    @Cacheable(value = "currencyRates", key = "#base + '-' + #target + '-' + #customerType", unless = "#result == null")
    public CurrencyExchangeRate getExchangeRate(String base, String target, String customerType) {
        logger.info("Checking cache for {} to {} for customer type {}", base, target, customerType);
        Optional<CurrencyExchangeRate> existingRate = currencyExchangeRateRepository.findByBaseAndTargetAndCustomerType(
                base, target, customerType);
        if (existingRate.isPresent()) {
            logger.info("Found exchange rate in database");
            return existingRate.get();
        }
        logger.info("Fetching from API");
        try {
            CurrencyExchangeRate newRate = fetchExchangeRateFromAPI(base, target, customerType);
            if (newRate != null) {
                currencyExchangeRateRepository.save(newRate);
            }
            return newRate;
        } catch (Exception e) {
            logger.error("Failed to fetch or save exchange rate for {}/{} ({}): {}", base, target, customerType, e.getMessage(), e);
            clearCacheForKey(base, target, customerType); // Evict cache on failure
            throw new RuntimeException("Failed to process exchange rate", e);
        }
    }

    public BigDecimal convertCurrency(String base, String target, BigDecimal amount, String customerType) {
        CurrencyExchangeRate rate = getExchangeRate(base, target, customerType);
        if (rate == null || rate.getFinalRate() == null) {
            throw new IllegalStateException("Exchange rate not available for " + base + "/" + target);
        }
        return amount.multiply(rate.getFinalRate()).setScale(4, java.math.RoundingMode.HALF_UP);
    }

    public boolean isValidCurrencyPair(String base, String target) {
        if (!SUPPORTED_CURRENCIES.contains(base) || !SUPPORTED_CURRENCIES.contains(target)) {
            logger.warn("Unsupported currency pair: {}/{}", base, target);
            return false;
        }
        try {
            CurrencyExchangeRate rate = fetchExchangeRateFromAPI(base, target, "standard");
            return rate != null && rate.getRate() != null;
        } catch (Exception e) {
            logger.warn("Validation failed for {}/{}: {}", base, target, e.getMessage());
            return false;
        }
    }

    // LIBOR Management Methods (unchanged)
    public BigDecimal getLiborSpreadNormal() { return liborSpreadNormal; }
    public BigDecimal getLiborSpreadSpecial() { return liborSpreadSpecial; }
    @Transactional public void setLiborSpreadNormal(BigDecimal newSpread) {
        if (newSpread.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("LIBOR spread cannot be negative");
        liborSpreadNormal = newSpread.setScale(6, java.math.RoundingMode.HALF_UP);
        logger.info("Updated LIBOR spread for normal customers to {}", liborSpreadNormal);
        clearAllCurrencyCache();
    }
    @Transactional public void setLiborSpreadSpecial(BigDecimal newSpread) {
        if (newSpread.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("LIBOR spread cannot be negative");
        liborSpreadSpecial = newSpread.setScale(6, java.math.RoundingMode.HALF_UP);
        logger.info("Updated LIBOR spread for special customers to {}", liborSpreadSpecial);
        clearAllCurrencyCache();
    }
    private BigDecimal calculateLiborSpread(String customerType) {
        return "special".equalsIgnoreCase(customerType) ? liborSpreadSpecial : liborSpreadNormal;
    }

    private CurrencyExchangeRate fetchExchangeRateFromAPI(String base, String target, String customerType) {
        if (!SUPPORTED_CURRENCIES.contains(base) || !SUPPORTED_CURRENCIES.contains(target)) {
            logger.error("Unsupported currency pair: {}/{}", base, target);
            return null;
        }
        String url = "https://data.fixer.io/api/latest?access_key=" + fixerApiKey + "&base=" + base + "&symbols=" + target;
        try {
            String response = restTemplate.getForObject(url, String.class);
            JsonNode json = objectMapper.readTree(response);
            if (!json.get("success").asBoolean()) {
                logger.error("Fixer API error for {}/{}: {}", base, target, json.get("error").get("message").asText());
                return null;
            }
            BigDecimal baseRate = new BigDecimal(json.get("rates").get(target).asText())
                    .setScale(6, java.math.RoundingMode.HALF_UP);
            BigDecimal liborSpread = calculateLiborSpread(customerType);
            BigDecimal finalRate = baseRate.add(baseRate.multiply(liborSpread))
                    .setScale(6, java.math.RoundingMode.HALF_UP);
            return new CurrencyExchangeRate(base, target, baseRate, finalRate, customerType);
        } catch (Exception e) {
            logger.error("Error fetching exchange rate for {}/{} ({}): {}", base, target, customerType, e.getMessage(), e);
            return null;
        }
    }

    @CacheEvict(value = "currencyRates", key = "#base + '-' + #target + '-' + #customerType")
    public void clearCacheForKey(String base, String target, String customerType) {
        logger.info("Cleared cache for {}/{} ({})", base, target, customerType);
    }

    @CacheEvict(value = {"currencyRates"}, allEntries = true)
    public void clearAllCurrencyCache() {
        logger.info("Manually cleared all currency caches");
    }
}