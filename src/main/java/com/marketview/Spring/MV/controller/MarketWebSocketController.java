package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class MarketWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(MarketWebSocketController.class);

    private final StockService stockService;
    private final SimpMessagingTemplate messagingTemplate;

    public MarketWebSocketController(StockService stockService, SimpMessagingTemplate messagingTemplate) {
        this.stockService = stockService;
        this.messagingTemplate = messagingTemplate;
    }

    // Handle client request for stock symbols
    @MessageMapping("/getSymbols")
    @SendTo("/topic/symbols")
    public List<Stock> sendStockSymbols() {
        logger.info("Received request for stock symbols via WebSocket");
        return stockService.getStockSymbols("US"); // Default to US exchange
    }

    // Periodically send wishlist updates (optional)
    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void sendWishlistUpdates() {
        logger.info("Sending wishlist updates via WebSocket");
        Iterable<Stock> wishlist = stockService.getWishlist();
        messagingTemplate.convertAndSend("/topic/wishlist", wishlist);
    }
}