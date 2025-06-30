package com.marketview.Spring.MV.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import com.marketview.Spring.MV.model.LiborRate;
import com.marketview.Spring.MV.repository.CurrencyExchangeRateRepository;
import com.marketview.Spring.MV.repository.CurrencyRepository;
import com.marketview.Spring.MV.repository.LiborRateRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class CurrencyService {

    @Value("${fixer.api.key}")
    private String fixerApiKey;

    @Value("${fixer.api.symbols.url}")
    private String fixerSymbolsUrl;

    @Value("${fixer.api.latest.url}")
    private String fixerLatestUrl;

    private static final long REDIS_TTL_SECONDS = 60 * 60; // 1 hour

    private final CurrencyExchangeRateRepository currencyExchangeRateRepository;
    private final CurrencyRepository currencyRepository;
    private final LiborRateRepository liborRateRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public CurrencyService(
            CurrencyExchangeRateRepository currencyExchangeRateRepository,
            CurrencyRepository currencyRepository,
            LiborRateRepository liborRateRepository,
            RedisTemplate<String, Object> redisTemplate,
            RestTemplate restTemplate,
            ObjectMapper objectMapper
    ) {
        this.currencyExchangeRateRepository = currencyExchangeRateRepository;
        this.currencyRepository = currencyRepository;
        this.liborRateRepository = liborRateRepository;
        this.redisTemplate = redisTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public void loadRates() {
        String url = fixerLatestUrl + "?access_key=" + fixerApiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            boolean success = jsonNode.get("success").asBoolean();

            if (!success) {
                log.error("Failed to load latest rates from Fixer.io");
                return;
            }

            String base = jsonNode.get("base").asText();
            JsonNode ratesNode = jsonNode.get("rates");

            Iterator<Map.Entry<String, JsonNode>> fields = ratesNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                String target = entry.getKey();
                BigDecimal rate = entry.getValue().decimalValue();

                saveRate(base, target, rate, "normal");
                saveRate(base, target, rate, "special");
            }

            log.info("Rates loaded and saved successfully for {} currencies.", ratesNode.size());

        } catch (Exception ex) {
            log.error("Error parsing latest rates from Fixer.io", ex);
        }
    }

    private void saveRate(String base, String target, BigDecimal rate, String customerType) {
        BigDecimal finalRate = calculateFinalRate(rate, customerType);
        CurrencyExchangeRate exchangeRate = new CurrencyExchangeRate(
                base, target, rate, finalRate, customerType
        );

        currencyExchangeRateRepository.save(exchangeRate);

        String redisKey = exchangeRate.getId();
        redisTemplate.opsForValue().set(redisKey, exchangeRate, REDIS_TTL_SECONDS, TimeUnit.SECONDS);
    }
    public Map<String, String> getAvailableSymbols() {
        String url = fixerSymbolsUrl + "?access_key=" + fixerApiKey;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        try {
            JsonNode jsonNode = objectMapper.readTree(response.getBody());
            boolean success = jsonNode.get("success").asBoolean();

            if (!success) {
                log.error("Failed to load symbols from Fixer.io");
                return Collections.emptyMap();
            }

            JsonNode symbolsNode = jsonNode.get("symbols");
            Map<String, String> symbols = new HashMap<>();
            symbolsNode.fields().forEachRemaining(entry -> {
                symbols.put(entry.getKey(), entry.getValue().asText());
            });
            return symbols;

        } catch (Exception ex) {
            log.error("Error parsing symbols from Fixer.io", ex);
            return Collections.emptyMap();
        }
    }

    private BigDecimal fetchRateFromApi(String base, String target) {
        String url = fixerLatestUrl
                + "?access_key=" + fixerApiKey
                + "&base=" + base
                + "&symbols=" + target;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode root = objectMapper.readTree(response.getBody());

            boolean success = root.path("success").asBoolean();
            if (!success) {
                log.error("Fixer API returned error: {}", response.getBody());
                return null;
            }

            JsonNode ratesNode = root.path("rates");
            if (ratesNode.has(target)) {
                return ratesNode.get(target).decimalValue();
            } else {
                log.warn("Target currency {} not found in Fixer API response.", target);
                return null;
            }
        } catch (Exception ex) {
            log.error("Error fetching rate from Fixer API", ex);
            return null;
        }
    }



    public CurrencyExchangeRate getExchangeRate(String base, String target, String customerType) {
        String key = generateId(base, target, customerType);
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        // STEP 1 — Try Redis
        Object cachedObject = ops.get(key);
        if (cachedObject != null) {
            CurrencyExchangeRate cached = objectMapper.convertValue(
                    cachedObject,
                    CurrencyExchangeRate.class
            );
            log.info("Returning exchange rate from Redis for key {}", key);
            return cached;
        }

        log.info("Cache miss for {}. Trying Fixer API.", key);

        // STEP 2 — Try fetching live from Fixer
        BigDecimal freshRate = fetchRateFromApi(base, target);
        if (freshRate != null) {
            BigDecimal finalRate = calculateFinalRate(freshRate, customerType);
            CurrencyExchangeRate updated = new CurrencyExchangeRate(
                    base, target, freshRate, finalRate, customerType
            );

            // Save/update in DB
            currencyExchangeRateRepository.save(updated);

            // Save in Redis
            ops.set(key, updated, REDIS_TTL_SECONDS, TimeUnit.SECONDS);

            log.info("Fetched fresh rate from Fixer and updated DB + Redis for key {}", key);
            return updated;
        }

        log.warn("Fixer API failed. Falling back to DB for key {}", key);

        // STEP 3 — Fallback to MongoDB
        Optional<CurrencyExchangeRate> fromDb = currencyExchangeRateRepository.findById(key);
        if (fromDb.isPresent()) {
            CurrencyExchangeRate dbRate = fromDb.get();
            ops.set(key, dbRate, REDIS_TTL_SECONDS, TimeUnit.SECONDS);
            log.info("Returning rate from DB fallback for key {}", key);
            return dbRate;
        }

        log.error("No exchange rate found even after Redis, Fixer, and DB for key {}", key);
        throw new RuntimeException("Rate not found for pair " + base + "/" + target);
    }

    public BigDecimal convertCurrency(String base, String target, BigDecimal amount, String customerType) {
        CurrencyExchangeRate rate = getExchangeRate(base, target, customerType);
        if (rate == null) {
            throw new RuntimeException("Rate not found for pair " + base + "/" + target);
        }
        return amount.multiply(rate.getFinalRate()).setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateFinalRate(BigDecimal rate, String customerType) {
        BigDecimal liborSpread = getLiborSpread(customerType);
        return rate.multiply(BigDecimal.ONE.add(liborSpread))
                .setScale(6, RoundingMode.HALF_UP);
    }

    private BigDecimal getLiborSpread(String customerType) {
        LiborRate liborRate = liborRateRepository.findById("LIBOR")
                .orElseGet(LiborRate::new);
        return "special".equalsIgnoreCase(customerType)
                ? liborRate.getSpecialRate()
                : liborRate.getNormalRate();
    }

    private String generateId(String base, String target, String customerType) {
        return String.format("%s-%s-%s",
                base.toUpperCase(),
                target.toUpperCase(),
                customerType.toLowerCase()
        );
    }
}
