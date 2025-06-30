package com.marketview.Spring.MV.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import com.marketview.Spring.MV.repository.CurrencyExchangeRateRepository;
import lombok.Getter;
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
import java.math.RoundingMode;
import java.util.Optional;
import java.util.Set;

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);
    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "EUR", "GBP", "JPY", "CAD", "INR");

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CurrencyExchangeRateRepository currencyExchangeRateRepository;

    @Value("${fixer.api.key}")
    private String fixerApiKey;

    // LIBOR Management Methods
    @Getter
    private BigDecimal liborSpreadNormal = new BigDecimal("0.005"); // Default 0.5%
    @Getter
    private BigDecimal liborSpreadSpecial = new BigDecimal("0.002"); // Default 0.2%

    public CurrencyService(RestTemplate restTemplate, ObjectMapper objectMapper,
                           CurrencyExchangeRateRepository currencyExchangeRateRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
    }

    @Cacheable(value = "currencyRates", key = "#base + '-' + #target + '-' + #customerType", unless = "#result == null")
    public CurrencyExchangeRate getExchangeRate(String base, String target, String customerType) {
        String normalizedBase = base.toUpperCase();
        String normalizedTarget = target.toUpperCase();
        String normalizedCustomerType = customerType.toLowerCase();
        String id = generateId(normalizedBase, normalizedTarget, normalizedCustomerType);
        logger.info("Checking cache for ID: {}", id);
        Optional<CurrencyExchangeRate> existingRate = currencyExchangeRateRepository.findById(id);
        if (existingRate.isPresent()) {
            logger.info("Found exchange rate in database for ID: {}", id);
            return existingRate.get();
        }
        logger.info("Fetching from API for ID: {}", id);
        try {
            CurrencyExchangeRate newRate = fetchExchangeRateFromAPI(normalizedBase, normalizedTarget, normalizedCustomerType);
            if (newRate != null) {
                currencyExchangeRateRepository.save(newRate);
                logger.info("Saved new rate to database for ID: {}", id);
            }
            return newRate;
        } catch (Exception e) {
            logger.error("Failed to fetch or save exchange rate for ID {}: {}", id, e.getMessage(), e);
            clearCacheForKey(base, target, customerType);
            throw new RuntimeException("Failed to process exchange rate", e);
        }
    }

    public BigDecimal convertCurrency(String base, String target, BigDecimal amount, String customerType) {
        CurrencyExchangeRate rate = getExchangeRate(base, target, customerType);
        if (rate == null || rate.getFinalRate() == null) {
            String id = generateId(base.toUpperCase(), target.toUpperCase(), customerType.toLowerCase());
            throw new IllegalStateException("Exchange rate not available for " + id + ". Please try again later.");
        }
        return amount.multiply(rate.getFinalRate()).setScale(4, RoundingMode.HALF_UP);
    }

    public boolean isValidCurrencyPair(String base, String target) {
        String normalizedBase = base.toUpperCase();
        String normalizedTarget = target.toUpperCase();
        if (!SUPPORTED_CURRENCIES.contains(normalizedBase) || !SUPPORTED_CURRENCIES.contains(normalizedTarget)) {
            logger.warn("Unsupported currency pair: {}/{}", normalizedBase, normalizedTarget);
            return false;
        }
        try {
            CurrencyExchangeRate rate = fetchExchangeRateFromAPI(normalizedBase, normalizedTarget, "standard");
            return rate != null && rate.getRate() != null;
        } catch (Exception e) {
            logger.warn("Validation failed for {}/{}: {}", normalizedBase, normalizedTarget, e.getMessage());
            return false;
        }
    }

    @Transactional
    public void setLiborSpreadNormal(BigDecimal newSpread) {
        if (newSpread.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("LIBOR spread cannot be negative");
        liborSpreadNormal = newSpread.setScale(6, RoundingMode.HALF_UP);
        logger.info("Updated LIBOR spread for normal customers to {}", liborSpreadNormal);
        clearAllCurrencyCache();
    }

    @Transactional
    public void setLiborSpreadSpecial(BigDecimal newSpread) {
        if (newSpread.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("LIBOR spread cannot be negative");
        liborSpreadSpecial = newSpread.setScale(6, RoundingMode.HALF_UP);
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
            if (response == null || response.trim().isEmpty()) {
                logger.error("Fixer API returned null or empty response for {}/{}", base, target);
                return null;
            }
            logger.debug("Fixer API response for {}/{}: {}", base, target, response);
            JsonNode json = objectMapper.readTree(response);
            if (!json.get("success").asBoolean()) {
                String errorMessage = json.has("error") && json.get("error").has("message")
                        ? json.get("error").get("message").asText("Unknown error") : "Unknown error";
                logger.error("Fixer API error for {}/{}: {}", base, target, errorMessage);
                return null;
            }
            JsonNode rates = json.get("rates");
            if (rates == null || !rates.has(target)) {
                logger.error("Fixer API response missing 'rates' or target currency {} for {}/{}", target, base, target);
                return null;
            }
            JsonNode rateNode = rates.get(target);
            if (rateNode == null || rateNode.isNull() || rateNode.asText().isEmpty()) {
                logger.error("Fixer API returned null or empty rate for {}/{}", base, target);
                return null;
            }
            BigDecimal baseRate = new BigDecimal(rateNode.asText()).setScale(6, RoundingMode.HALF_UP);
            BigDecimal liborSpread = calculateLiborSpread(customerType);
            BigDecimal finalRate = baseRate.add(baseRate.multiply(liborSpread)).setScale(6, RoundingMode.HALF_UP);
            return new CurrencyExchangeRate(base, target, baseRate, finalRate, customerType);
        } catch (Exception e) {
            logger.error("Error fetching exchange rate for {}/{} ({}): {}", base, target, customerType, e.getMessage(), e);
            return null;
        }
    }

    @CacheEvict(value = "currencyRates", key = "#base + '-' + #target + '-' + #customerType")
    public void clearCacheForKey(String base, String target, String customerType) {
        String id = generateId(base, target, customerType);
        logger.info("Cleared cache for ID: {}", id);
    }

    @CacheEvict(value = "currencyRates", allEntries = true)
    public void clearAllCurrencyCache() {
        logger.info("Manually cleared all currency caches");
    }

    @Scheduled(fixedRate = 600000) // Every 10 minutes, matching TTL
    @CacheEvict(value = "currencyRates", allEntries = true)
    public void evictAllCurrencyCaches() {
        logger.info("Scheduled eviction of all currency caches");
    }

    private static String generateId(String base, String target, String customerType) {
        return String.format("%s-%s-%s", base.toUpperCase(), target.toUpperCase(), customerType.toLowerCase());
    }
}