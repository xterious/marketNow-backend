package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.model.UserWishlist;
import com.marketview.Spring.MV.service.StockService;
import com.marketview.Spring.MV.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.List;
@EnableAsync
@Controller
public class MarketWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(MarketWebSocketController.class);

    private final StockService stockService;
    private final SimpMessagingTemplate messagingTemplate;
    private final UserService userService;

    public MarketWebSocketController(StockService stockService, SimpMessagingTemplate messagingTemplate, UserService userService) {
        this.stockService = stockService;
        this.messagingTemplate = messagingTemplate;
        this.userService = userService;
    }

    @MessageMapping("/getSymbols/{exchange}")
    @SendTo("/topic/symbols/{exchange}")
    public List<Stock> sendStockSymbols(@DestinationVariable String exchange) {
        logger.info("Received request for stock symbols via WebSocket for exchange: {}", exchange);
        return stockService.getStockSymbols(exchange);
    }

    @MessageMapping("/wishlist/update/{username}")
    @SendTo("/topic/wishlist/{username}")
    public UserWishlist sendWishlistUpdate(@DestinationVariable String username, Principal principal) {
        logger.info("Received wishlist update request for username: {}", username);
        if (principal == null || !principal.getName().equals(username)) {
            logger.warn("Unauthorized access attempt for username: {}", username);
            throw new SecurityException("Unauthorized access to wishlist");
        }
        UserWishlist wishlist = stockService.getWishlist(username);
        if (wishlist == null) {
            logger.warn("No wishlist found for username: {}", username);
            throw new IllegalStateException("Wishlist not found");
        }
        return wishlist;
    }

    @Scheduled(fixedRate = 30000) // Every 30 seconds
    public void sendPeriodicWishlistUpdates() {
        logger.info("Sending periodic wishlist updates via WebSocket");
        List<String> activeUsernames = userService.getAllUsernames();
        if (activeUsernames == null || activeUsernames.isEmpty()) {
            logger.warn("No active usernames found for wishlist updates");
            return;
        }
        for (String username : activeUsernames) {
            UserWishlist wishlist = stockService.getWishlist(username);
            if (wishlist != null) {
                messagingTemplate.convertAndSend("/topic/wishlist/" + username, wishlist);
                logger.debug("Sent wishlist update for username: {}", username);
            } else {
                logger.warn("No wishlist data for username: {}", username);
            }
        }
    }
}