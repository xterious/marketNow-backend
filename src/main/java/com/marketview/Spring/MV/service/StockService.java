package com.marketview.Spring.MV.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockService.class);

    private final StockRepository stockRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
        this.restTemplate = new RestTemplate();
    }

    // Fetch stock symbols for a given exchange, with quotes for limited symbols
    public List<Stock> getStockSymbols(String exchange) {
        String url = "https://finnhub.io/api/v1/stock/symbol?exchange=" + exchange + "&token=" + finnhubApiKey;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            JsonNode jsonArray = objectMapper.readTree(response.getBody());

            if (!jsonArray.isArray()) {
                logger.error("Unexpected response format for exchange: {}", exchange);
                throw new RuntimeException("Invalid response format from Finnhub");
            }

            List<Stock> stocks = new ArrayList<>();
            int count = 0;
            int maxQuoteRequests = 10; // Limit quote requests to avoid rate limits
            for (JsonNode node : jsonArray) {
                String symbol = node.has("symbol") ? node.get("symbol").asText() : null;
                String currency = node.has("currency") ? node.get("currency").asText() : null;
                String description = node.has("description") ? node.get("description").asText() : null;
                String displaySymbol = node.has("displaySymbol") ? node.get("displaySymbol").asText() : null;
                String figi = node.has("figi") ? node.get("figi").asText() : null;
                String mic = node.has("mic") ? node.get("mic").asText() : null;
                String type = node.has("type") ? node.get("type").asText() : null;
                Double currentPrice = null;
                Double change = null;
                Double percentChange = null;
                Double high = null;
                Double low = null;
                Double open = null;
                Double previousClose = null;

                if (symbol != null && count < maxQuoteRequests) {
                    try {
                        Stock quote = fetchStockQuote(symbol);
                        currentPrice = quote.getCurrentPrice();
                        change = quote.getChange();
                        percentChange = quote.getPercentChange();
                        high = quote.getHigh();
                        low = quote.getLow();
                        open = quote.getOpen();
                        previousClose = quote.getPreviousClose();
                        count++;
                    } catch (Exception e) {
                        logger.warn("Failed to fetch quote for {}: {}", symbol, e.getMessage());
                    }
                }

                if (symbol != null) {
                    stocks.add(new Stock(symbol, currency, description, displaySymbol, figi, mic, type,
                            currentPrice, change, percentChange, high, low, open, previousClose));
                }
            }

            logger.info("Fetched {} stock symbols for exchange: {}, with quotes for {} symbols", stocks.size(), exchange, count);
            return stocks;

        } catch (HttpClientErrorException e) {
            logger.error("HTTP error fetching stock symbols for exchange {}: {}", exchange, e.getMessage());
            throw new RuntimeException("Failed to fetch stock symbols: " + e.getStatusCode());
        } catch (Exception e) {
            logger.error("Error fetching stock symbols for exchange {}: {}", exchange, e.getMessage());
            throw new RuntimeException("Error fetching stock symbols", e);
        }
    }

    // Fetch quote data for a single stock symbol
    private Stock fetchStockQuote(String symbol) {
        String quoteUrl = "https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + finnhubApiKey;

        try {
            // Fetch quote data
            ResponseEntity<String> quoteResponse = restTemplate.getForEntity(quoteUrl, String.class);
            JsonNode quoteJson = objectMapper.readTree(quoteResponse.getBody());
            Double currentPrice = quoteJson.has("c") && !quoteJson.get("c").isNull() ? quoteJson.get("c").asDouble() : null;
            Double change = quoteJson.has("d") && !quoteJson.get("d").isNull() ? quoteJson.get("d").asDouble() : null;
            Double percentChange = quoteJson.has("dp") && !quoteJson.get("dp").isNull() ? quoteJson.get("dp").asDouble() : null;
            Double high = quoteJson.has("h") && !quoteJson.get("h").isNull() ? quoteJson.get("h").asDouble() : null;
            Double low = quoteJson.has("l") && !quoteJson.get("l").isNull() ? quoteJson.get("l").asDouble() : null;
            Double open = quoteJson.has("o") && !quoteJson.get("o").isNull() ? quoteJson.get("o").asDouble() : null;
            Double previousClose = quoteJson.has("pc") && !quoteJson.get("pc").isNull() ? quoteJson.get("pc").asDouble() : null;

            // Fetch symbol details directly for the specific symbol
            String symbolUrl = "https://finnhub.io/api/v1/stock/symbol?exchange=US&symbol=" + symbol + "&token=" + finnhubApiKey;
            ResponseEntity<String> symbolResponse = restTemplate.getForEntity(symbolUrl, String.class);
            JsonNode symbolJson = objectMapper.readTree(symbolResponse.getBody());

            // Initialize default values
            String currency = null;
            String description = symbol;
            String displaySymbol = symbol;
            String figi = null;
            String mic = null;
            String type = null;

            // Parse symbol details if available
            if (symbolJson.isArray() && symbolJson.size() > 0) {
                JsonNode node = symbolJson.get(0);
                currency = node.has("currency") ? node.get("currency").asText() : null;
                description = node.has("description") ? node.get("description").asText() : symbol;
                displaySymbol = node.has("displaySymbol") ? node.get("displaySymbol").asText() : symbol;
                figi = node.has("figi") ? node.get("figi").asText() : null;
                mic = node.has("mic") ? node.get("mic").asText() : null;
                type = node.has("type") ? node.get("type").asText() : null;
            }

            return new Stock(symbol, currency, description, displaySymbol, figi, mic, type,
                    currentPrice, change, percentChange, high, low, open, previousClose);
        } catch (Exception e) {
            logger.error("Error fetching quote for {}: {}", symbol, e.getMessage());
            throw new RuntimeException("Failed to fetch quote for " + symbol, e);
        }
    }

    // Get quote for a specific stock
    public Stock getStockQuote(String symbol) {
        return fetchStockQuote(symbol);
    }

    // Add stock to wishlist with quote data
    public Stock addToWishlist(String symbol) {
        if (stockRepository.existsBySymbol(symbol)) {
            return stockRepository.findById(symbol).orElse(null);
        }

        Stock stock = fetchStockQuote(symbol);
        return stockRepository.save(stock);
    }

    // Remove stock from wishlist
    public void removeFromWishlist(String symbol) {
        stockRepository.deleteById(symbol);
    }

    // Get all stocks in wishlist
    public Iterable<Stock> getWishlist() {
        return stockRepository.findAll();
    }
}