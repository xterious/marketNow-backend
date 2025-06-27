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

@Service
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final CurrencyExchangeRateRepository currencyExchangeRateRepository;

    @Value("${fixer.api.key}")
    private String fixerApiKey;

    public CurrencyService(RestTemplate restTemplate, ObjectMapper objectMapper, 
                          CurrencyExchangeRateRepository currencyExchangeRateRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
    }

    // Cache-first approach for currency exchange rates
    @Cacheable(value = "currencyRates", key = "#base + '-' + #target + '-' + #customerType")
    public CurrencyExchangeRate getExchangeRate(String base, String target, String customerType) {
        logger.info("Checking cache for {} to {} for customer type {}", base, target, customerType);

        // Check if the exchange rate exists in the database
        Optional<CurrencyExchangeRate> existingRate = currencyExchangeRateRepository.findByBaseAndTargetAndCustomerType(
                base, target, customerType);

        if (existingRate.isPresent()) {
            logger.info("Found exchange rate in database for {} to {} for customer type {}", base, target, customerType);
            return existingRate.get();
        }

        // If not in database, fetch from API
        logger.info("Exchange rate not found in database. Fetching from API for {} to {} for customer type {}", base, target, customerType);
        CurrencyExchangeRate newRate = fetchExchangeRateFromAPI(base, target, customerType);

        // Save to database for future use
        currencyExchangeRateRepository.save(newRate);
        logger.info("Saved new exchange rate to database for {} to {} for customer type {}", base, target, customerType);

        return newRate;
    }

    // Get multiple exchange rates for a base currency
    @Cacheable(value = "currencyRatesMultiple", key = "#base + '-' + #targets.toString() + '-' + #customerType")
    public List<CurrencyExchangeRate> getMultipleExchangeRates(String base, List<String> targets, String customerType) {
        logger.info("Checking cache for {} to multiple targets for customer type {}", base, customerType);

        List<CurrencyExchangeRate> result = new ArrayList<>();
        List<String> targetsToFetch = new ArrayList<>();

        // Check which rates exist in the database
        for (String target : targets) {
            Optional<CurrencyExchangeRate> existingRate = currencyExchangeRateRepository.findByBaseAndTargetAndCustomerType(
                    base, target, customerType);

            if (existingRate.isPresent()) {
                logger.info("Found exchange rate in database for {} to {} for customer type {}", base, target, customerType);
                result.add(existingRate.get());
            } else {
                targetsToFetch.add(target);
            }
        }

        // If all rates were found in the database, return them
        if (targetsToFetch.isEmpty()) {
            return result;
        }

        // Fetch missing rates from API
        logger.info("Fetching {} missing exchange rates from API for base {} and customer type {}", 
                  targetsToFetch.size(), base, customerType);
        List<CurrencyExchangeRate> newRates = fetchMultipleExchangeRatesFromAPI(base, targetsToFetch, customerType);

        // Save new rates to database
        for (CurrencyExchangeRate newRate : newRates) {
            currencyExchangeRateRepository.save(newRate);
            logger.info("Saved new exchange rate to database for {} to {} for customer type {}", 
                      base, newRate.getTarget(), customerType);
            result.add(newRate);
        }

        return result;
    }

    // Get all supported currencies
    @Cacheable(value = "supportedCurrencies")
    public List<String> getSupportedCurrencies() {
        logger.info("Checking cache for supported currencies");
        logger.info("Cache miss for supported currencies. Fetching from API.");
        List<String> currencies = fetchSupportedCurrenciesFromAPI();
        logger.info("Fetched {} supported currencies from API", currencies.size());
        return currencies;
    }

    // Get historical exchange rate
    @Cacheable(value = "historicalRates", key = "#base + '-' + #target + '-' + #date + '-' + #customerType")
    public CurrencyExchangeRate getHistoricalExchangeRate(String base, String target, String date, String customerType) {
        logger.info("Checking cache for historical rate for {} to {} on date {} for customer type {}", base, target, date, customerType);

        // Historical rates are not stored in the database in the same way as current rates
        // We could implement a more sophisticated storage mechanism for historical rates if needed

        logger.info("Cache miss for historical rate. Fetching from API for {} to {} on date {} for customer type {}", 
                  base, target, date, customerType);
        CurrencyExchangeRate historicalRate = fetchHistoricalExchangeRateFromAPI(base, target, date, customerType);
        logger.info("Fetched historical rate from API for {} to {} on date {}", base, target, date);

        return historicalRate;
    }

    // Convert amount between currencies
    public BigDecimal convertCurrency(String base, String target, BigDecimal amount, String customerType) {
        CurrencyExchangeRate rate = getExchangeRate(base, target, customerType);
        return amount.multiply(rate.getFinalRate()).setScale(4, BigDecimal.ROUND_HALF_UP);
    }

    // Get exchange rate from database (for cached/stored rates)
    public Optional<CurrencyExchangeRate> getStoredExchangeRate(String base, String target, String customerType) {
        return currencyExchangeRateRepository.findByBaseAndTargetAndCustomerType(base, target, customerType);
    }

    // Get all rates for a specific base currency
    public List<CurrencyExchangeRate> getRatesForBaseCurrency(String baseCurrency) {
        return currencyExchangeRateRepository.findByBase(baseCurrency);
    }

    // Get all rates for a specific target currency
    public List<CurrencyExchangeRate> getRatesForTargetCurrency(String targetCurrency) {
        return currencyExchangeRateRepository.findByTarget(targetCurrency);
    }

    // Private method to fetch exchange rate from API
    private CurrencyExchangeRate fetchExchangeRateFromAPI(String base, String target, String customerType) {
        String url = "https://data.fixer.io/api/latest?access_key=" + fixerApiKey + 
                    "&base=" + base + "&symbols=" + target;

        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.debug("API Response: {}", response);

            JsonNode json = objectMapper.readTree(response);
            if (!json.get("success").asBoolean()) {
                logger.error("Fixer API error: {}", json.get("error").get("message").asText());
                throw new RuntimeException("Fixer API error: " + json.get("error").get("message").asText());
            }

            BigDecimal rate = new BigDecimal(json.get("rates").get(target).asText());
            CurrencyExchangeRate rateObj = new CurrencyExchangeRate(base, target, rate, customerType);

            logger.debug("Created CurrencyExchangeRate: {}", rateObj);
            return rateObj;
        } catch (Exception e) {
            logger.error("Error fetching exchange rate: {}", e.getMessage());
            throw new RuntimeException("Error fetching exchange rate", e);
        }
    }

    // Private method to fetch multiple exchange rates from API
    private List<CurrencyExchangeRate> fetchMultipleExchangeRatesFromAPI(String base, List<String> targets, String customerType) {
        String symbols = String.join(",", targets);
        String url = "https://data.fixer.io/api/latest?access_key=" + fixerApiKey + 
                    "&base=" + base + "&symbols=" + symbols;

        List<CurrencyExchangeRate> rates = new ArrayList<>();

        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.debug("API Response for multiple rates: {}", response);

            JsonNode json = objectMapper.readTree(response);
            if (!json.get("success").asBoolean()) {
                logger.error("Fixer API error: {}", json.get("error").get("message").asText());
                throw new RuntimeException("Fixer API error: " + json.get("error").get("message").asText());
            }

            JsonNode ratesNode = json.get("rates");
            for (String target : targets) {
                if (ratesNode.has(target)) {
                    BigDecimal rate = new BigDecimal(ratesNode.get(target).asText());
                    CurrencyExchangeRate rateObj = new CurrencyExchangeRate(base, target, rate, customerType);
                    rates.add(rateObj);
                }
            }

            logger.debug("Created {} CurrencyExchangeRates", rates.size());
            return rates;
        } catch (Exception e) {
            logger.error("Error fetching multiple exchange rates: {}", e.getMessage());
            throw new RuntimeException("Error fetching multiple exchange rates", e);
        }
    }

    // Private method to fetch supported currencies from API
    private List<String> fetchSupportedCurrenciesFromAPI() {
        String url = "https://data.fixer.io/api/symbols?access_key=" + fixerApiKey;

        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.debug("API Response for supported currencies: {}", response);

            JsonNode json = objectMapper.readTree(response);
            if (!json.get("success").asBoolean()) {
                logger.error("Fixer API error: {}", json.get("error").get("message").asText());
                throw new RuntimeException("Fixer API error: " + json.get("error").get("message").asText());
            }

            List<String> currencies = new ArrayList<>();
            JsonNode symbolsNode = json.get("symbols");
            symbolsNode.fieldNames().forEachRemaining(currencies::add);

            logger.info("Fetched {} supported currencies", currencies.size());
            return currencies;
        } catch (Exception e) {
            logger.error("Error fetching supported currencies: {}", e.getMessage());
            throw new RuntimeException("Error fetching supported currencies", e);
        }
    }

    // Private method to fetch historical exchange rate from API
    private CurrencyExchangeRate fetchHistoricalExchangeRateFromAPI(String base, String target, String date, String customerType) {
        String url = "https://data.fixer.io/api/" + date + "?access_key=" + fixerApiKey + 
                    "&base=" + base + "&symbols=" + target;

        try {
            String response = restTemplate.getForObject(url, String.class);
            logger.debug("API Response for historical rate: {}", response);

            JsonNode json = objectMapper.readTree(response);
            if (!json.get("success").asBoolean()) {
                logger.error("Fixer API error: {}", json.get("error").get("message").asText());
                throw new RuntimeException("Fixer API error: " + json.get("error").get("message").asText());
            }

            BigDecimal rate = new BigDecimal(json.get("rates").get(target).asText());
            CurrencyExchangeRate rateObj = new CurrencyExchangeRate(base, target, rate, customerType);

            logger.debug("Created historical CurrencyExchangeRate: {}", rateObj);
            return rateObj;
        } catch (Exception e) {
            logger.error("Error fetching historical exchange rate: {}", e.getMessage());
            throw new RuntimeException("Error fetching historical exchange rate", e);
        }
    }

    // Scheduled task: Update currency rates in database every 30 minutes
    @Scheduled(fixedRate = 1800000) // 30 minutes
    @Transactional
    public void updateCurrencyRatesInDatabase() {
        logger.info("Starting scheduled currency rates update");

        try {
            // Update existing currency pairs with latest rates
            List<CurrencyExchangeRate> existingRates = currencyExchangeRateRepository.findAll();

            for (CurrencyExchangeRate existingRate : existingRates) {
                try {
                    CurrencyExchangeRate updatedRate = fetchExchangeRateFromAPI(
                            existingRate.getBase(), 
                            existingRate.getTarget(), 
                            existingRate.getCustomerType()
                    );

                    // Update the existing rate entity with new data
                    existingRate.setRate(updatedRate.getRate());
                    existingRate.setFinalRate(updatedRate.getFinalRate());
                    currencyExchangeRateRepository.save(existingRate);
                } catch (Exception e) {
                    logger.warn("Failed to update currency rate for {}/{}", 
                              existingRate.getBase(), existingRate.getTarget());
                }
            }

            logger.info("Successfully updated {} currency rates in database", existingRates.size());
        } catch (Exception e) {
            logger.error("Error during scheduled currency update: {}", e.getMessage(), e);
        }
    }

    // Scheduled task: Clean up stale rates (older than 24 hours)
    @Scheduled(cron = "0 0 3 * * ?") // Daily at 3 AM
    @Transactional
    public void cleanupStaleRates() {
        logger.info("Starting cleanup of stale currency rates");

        try {
            long oneDayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000L);
            List<CurrencyExchangeRate> staleRates = currencyExchangeRateRepository.findStaleRates(oneDayAgo);

            if (!staleRates.isEmpty()) {
                // Update stale rates instead of deleting them
                for (CurrencyExchangeRate staleRate : staleRates) {
                    try {
                        CurrencyExchangeRate updatedRate = fetchExchangeRateFromAPI(
                                staleRate.getBase(),
                                staleRate.getTarget(),
                                staleRate.getCustomerType()
                        );
                        staleRate.setRate(updatedRate.getRate());
                        staleRate.setFinalRate(updatedRate.getFinalRate());
                        currencyExchangeRateRepository.save(staleRate);
                    } catch (Exception e) {
                        logger.warn("Failed to refresh stale rate for {}/{}", 
                                  staleRate.getBase(), staleRate.getTarget());
                    }
                }
                logger.info("Refreshed {} stale currency rates", staleRates.size());
            } else {
                logger.info("No stale currency rates found");
            }
        } catch (Exception e) {
            logger.error("Error during stale rates cleanup: {}", e.getMessage(), e);
        }
    }

    // Manual cache eviction methods
    @CacheEvict(value = "currencyRates", allEntries = true)
    public void clearCurrencyCache() {
        logger.info("Manually cleared currency rates cache");
    }

    @CacheEvict(value = "currencyRatesMultiple", allEntries = true)
    public void clearMultipleCurrencyCache() {
        logger.info("Manually cleared multiple currency rates cache");
    }

    @CacheEvict(value = "supportedCurrencies")
    public void clearSupportedCurrenciesCache() {
        logger.info("Manually cleared supported currencies cache");
    }

    @CacheEvict(value = {"currencyRates", "currencyRatesMultiple", "supportedCurrencies", "historicalRates"}, allEntries = true)
    public void clearAllCurrencyCache() {
        logger.info("Manually cleared all currency caches");
    }

    @CacheEvict(value = "historicalRates", allEntries = true)
    public void clearHistoricalRatesCache() {
        logger.info("Manually cleared historical rates cache");
    }

    // Utility methods
    public boolean isValidCurrencyPair(String base, String target) {
        try {
            CurrencyExchangeRate rate = fetchExchangeRateFromAPI(base, target, "standard");
            return rate != null && rate.getRate() != null;
        } catch (Exception e) {
            return false;
        }
    }

    public List<String> getPopularCurrencyPairs() {
        // Return commonly traded currency pairs
        return List.of("USD-EUR", "USD-GBP", "USD-JPY", "EUR-GBP", "USD-CAD", 
                      "USD-AUD", "USD-CHF", "EUR-JPY", "GBP-JPY", "AUD-JPY");
    }
}
