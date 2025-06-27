package com.marketview.Spring.MV.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.util.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    public StockService(RestTemplate restTemplate,
                        ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "stockSymbols", key = "#exchange")
    public List<Stock> getStockSymbols(String exchange) {
        String url = "https://finnhub.io/api/v1/stock/symbol?exchange=" + exchange + "&token=" + finnhubApiKey;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode jsonArray = objectMapper.readTree(response.getBody());
            if (!jsonArray.isArray()) throw new RuntimeException("Invalid response format");
            List<Stock> stocks = new ArrayList<>();
            int count = 0;
            int maxQuoteRequests = 10;
            for (JsonNode node : jsonArray) {
                String symbol = node.has("symbol") ? node.get("symbol").asText() : null;
                if (symbol != null) {
                    Stock stock = fetchStockQuoteSync(symbol);
                    stocks.add(stock);
                    if (++count >= maxQuoteRequests) break;
                }
            }
            logger.info("Fetched {} stock symbols for exchange: {}", stocks.size(), exchange);
            return stocks;
        } catch (Exception e) {
            logger.error("Error fetching stock symbols: {}", e.getMessage());
            throw new RuntimeException("Error fetching stock symbols", e);
        }
    }

    @Cacheable(value = "stockQuotes", key = "#symbol")
    @Async
    public CompletableFuture<Stock> getStockQuote(String symbol) {
        return CompletableFuture.supplyAsync(() -> fetchStockQuoteSync(symbol));
    }

    @CachePut(value = "stockQuotes", key = "#result.symbol")
    public Stock fetchStockQuoteSync(String symbol) {
        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + finnhubApiKey;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode json = objectMapper.readTree(response.getBody());
            Double currentPrice = json.has("c") ? json.get("c").asDouble() : null;
            return new Stock(symbol, "USD", symbol, symbol, null, null, "Common Stock",
                    currentPrice, null, null, null, null, null, null);
        } catch (Exception e) {
            logger.error("Error fetching quote for {}: {}", symbol, e.getMessage());
            throw new CustomException("Error fetching quote for " + symbol, org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CacheEvict(value = "stockQuotes", key = "#symbol")
    public void clearQuoteCache(String symbol) {
        logger.info("Cleared cache for symbol: {}", symbol);
    }

}
