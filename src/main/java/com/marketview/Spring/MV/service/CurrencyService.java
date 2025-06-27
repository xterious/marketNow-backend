package com.marketview.Spring.MV.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

@Service
@Data
public class CurrencyService {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${fixer.api.key}")
    private String fixerApiKey;

    public CurrencyService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "currencyRates", key = "#base + '-' + #target + '-' + #customerType")
    public CurrencyExchangeRate getExchangeRate(String base, String target, String customerType) {
        logger.info("Fetching exchange rate for {} to {} for customer type {}", base, target, customerType);
        String url = "https://data.fixer.io/api/latest?access_key=" + fixerApiKey + "&base=" + base + "&symbols=" + target;
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
}