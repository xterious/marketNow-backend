package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.service.WishlistService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
public class WishlistWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(WishlistWebSocketController.class);
    private final WishlistService wishlistService;

    public WishlistWebSocketController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @MessageMapping("/wishlist")
    @SendTo("/topic/wishlist")
    public WishlistResponse getWishlist(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        logger.info("Fetching wishlist for user: {}", username);
        return new WishlistResponse(
            wishlistService.getCurrencyWishlist(username).getFavoriteCurrencies(),
            wishlistService.getStockWishlist(username).getFavoriteStocks(),
            wishlistService.getNewsWishlist(username).getFavoriteNews()
        );
    }

    public static class WishlistResponse {
        private Set<String> currencies;
        private Set<String> stocks;
        private Set<String> news;
        public WishlistResponse(Set<String> currencies, Set<String> stocks, Set<String> news) {
            this.currencies = currencies;
            this.stocks = stocks;
            this.news = news;
        }
        public Set<String> getCurrencies() { return currencies; }
        public void setCurrencies(Set<String> currencies) { this.currencies = currencies; }
        public Set<String> getStocks() { return stocks; }
        public void setStocks(Set<String> stocks) { this.stocks = stocks; }
        public Set<String> getNews() { return news; }
        public void setNews(Set<String> news) { this.news = news; }
    }
}