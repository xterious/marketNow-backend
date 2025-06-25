package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.model.UserWishlist;
import com.marketview.Spring.MV.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/symbols")
    public List<Stock> getStockSymbols(
            @RequestParam @Pattern(regexp = "^[A-Z]{2,4}$", message = "Exchange must be 2-4 uppercase letters") String exchange,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "100") @Max(1000) int size) {
        List<Stock> stocks = stockService.getStockSymbols(exchange);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, stocks.size());
        return stocks.subList(fromIndex, toIndex);
    }

    @GetMapping("/quote")
    public CompletableFuture<Stock> getStockQuote(@RequestParam String symbol) {
        return stockService.getStockQuote(symbol);
    }

    @PostMapping("/wishlist/{type}/{item}")
    public ResponseEntity<UserWishlist> addToWishlist(@PathVariable String type, @PathVariable String item) {
        // Authentication check should be handled by SecurityConfig
        String username = "TODO: Get from Authentication principal"; // Replace with actual logic
        UserWishlist updatedWishlist = stockService.addToWishlist(username, item, type);
        messagingTemplate.convertAndSend("/topic/wishlist/" + username, updatedWishlist);
        return ResponseEntity.ok(updatedWishlist);
    }

    @DeleteMapping("/wishlist/{type}/{item}")
    public ResponseEntity<UserWishlist> removeFromWishlist(@PathVariable String type, @PathVariable String item) {
        // Authentication check should be handled by SecurityConfig
        String username = "TODO: Get from Authentication principal"; // Replace with actual logic
        UserWishlist updatedWishlist = stockService.removeFromWishlist(username, item, type);
        messagingTemplate.convertAndSend("/topic/wishlist/" + username, updatedWishlist);
        return ResponseEntity.ok(updatedWishlist);
    }

    // Removed @MessageMapping("/wishlist/update/{username}") to avoid conflict
}