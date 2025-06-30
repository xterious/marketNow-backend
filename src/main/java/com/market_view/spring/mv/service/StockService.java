package com.market_view.spring.mv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market_view.spring.mv.model.Stock;
import com.market_view.spring.mv.repository.StockRepository;
import com.market_view.spring.mv.util.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final StockRepository stockRepository;

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    public StockService(RestTemplate restTemplate, ObjectMapper objectMapper, StockRepository stockRepository) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.stockRepository = stockRepository;
    }

    // Cache-first approach for stock symbols
    @Cacheable(value = "stockSymbols", key = "#exchange")
    public List<Stock> getStockSymbols(String exchange) {
        logger.info("Cache miss for exchange: {}. Fetching from API.", exchange);
        return fetchStockSymbolsFromAPI(exchange);
    }

    // Cache-first approach for individual stock quotes
    @Cacheable(value = "stockQuotes", key = "#symbol")
    public Stock getStockQuote(String symbol) {
        logger.info("Cache miss for symbol: {}. Fetching from API.", symbol);
        return fetchStockQuoteFromAPI(symbol);
    }

    // Private method to fetch stock symbols from API
    private List<Stock> fetchStockSymbolsFromAPI(String exchange) {
        String url = "https://finnhub.io/api/v1/stock/symbol?exchange=" + exchange + "&token=" + finnhubApiKey;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode jsonArray = objectMapper.readTree(response.getBody());
            if (!jsonArray.isArray()) throw new RuntimeException("Invalid response format");

            List<Stock> stocks = new ArrayList<>();
            int count = 0;
            int maxQuoteRequests = 10; // Limit API calls

            for (JsonNode node : jsonArray) {
                String symbol = node.has("symbol") ? node.get("symbol").asText() : null;
                if (symbol != null) {
                    Stock stock = fetchStockQuoteFromAPI(symbol);
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

    // Private method to fetch individual stock quote from API
    private Stock fetchStockQuoteFromAPI(String symbol) {
        String url = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + finnhubApiKey;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            // Only stockSymbol is available here, so set others as null or default
            return new Stock(symbol, null, null, null);
        } catch (Exception e) {
            logger.error("Error fetching quote for {}: {}", symbol, e.getMessage());
            throw new CustomException("Error fetching quote for " + symbol, 
                    org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Private method to fetch individual stock quote from API with exchange information


    // Scheduled task: Update stock data in database every hour
    @Scheduled(fixedRate = 3600000) // 1 hour
    @Transactional
    public void updateStockDataInDatabase() {
        logger.info("Starting scheduled stock data update");

        try {
            // Update existing stocks with latest data
            List<Stock> existingStocks = stockRepository.findAll();

            for (Stock existingStock : existingStocks) {
                try {
                    Stock updatedStock = fetchStockQuoteFromAPI(existingStock.getStockSymbol());
                    // Update the existing stock entity with new data (only fields that exist)
                    existingStock.setMarketOrigin(updatedStock.getMarketOrigin());
                    existingStock.setStockName(updatedStock.getStockName());
                    existingStock.setType(updatedStock.getType());
                    stockRepository.save(existingStock);
                } catch (Exception e) {
                    logger.warn("Failed to update stock data for symbol: {}", existingStock.getStockSymbol());
                }
            }

            logger.info("Successfully updated {} stocks in database", existingStocks.size());
        } catch (Exception e) {
            logger.error("Error during scheduled stock update: {}", e.getMessage(), e);
        }
    }

    // Manual cache eviction
    @CacheEvict(value = {"stockSymbols", "stockQuotes"}, allEntries = true)
    public void clearStockCache() {
        logger.info("Manually cleared stock cache");
    }

    // Save a Stock entity to MongoDB
    public Stock saveStock(Stock stock) {
        return stockRepository.save(stock);
    }
}
