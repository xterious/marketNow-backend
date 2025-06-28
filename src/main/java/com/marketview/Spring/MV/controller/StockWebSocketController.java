package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class StockWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(StockWebSocketController.class);
    private final StockService stockService;

    public StockWebSocketController(StockService stockService) {
        this.stockService = stockService;
    }

    @MessageMapping("/stocks")
    @SendTo("/topic/stocks")
    public List<Stock> getStockSymbols(StockRequest request) {
        logger.info("Fetching stock symbols for exchange: {}", request.getExchange());
        return stockService.getStockSymbols(request.getExchange());
    }

    @MessageMapping("/stock/quote")
    @SendTo("/topic/stock/quote")
    public Stock getStockQuote(StockQuoteRequest request) {
        logger.info("Fetching stock quote for symbol: {}", request.getSymbol());
        return stockService.getStockQuote(request.getSymbol().toUpperCase());
    }

    public static class StockRequest {
        private String exchange;
        public String getExchange() { return exchange; }
        public void setExchange(String exchange) { this.exchange = exchange; }
    }

    public static class StockQuoteRequest {
        private String symbol;
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
    }
}