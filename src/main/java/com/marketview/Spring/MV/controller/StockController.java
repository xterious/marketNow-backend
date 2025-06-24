package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.service.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // Fetch stock symbols for an exchange
    @GetMapping("/symbols")
    public List<Stock> getStockSymbols(@RequestParam String exchange) {
        return stockService.getStockSymbols(exchange);
    }

    // Fetch quote for a specific stock
    @GetMapping("/quote")
    public Stock getStockQuote(@RequestParam String symbol) {
        return stockService.getStockQuote(symbol);
    }

    // Add to wishlist
    @PostMapping("/wishlist/{symbol}")
    public Stock addToWishlist(@PathVariable String symbol) {
        return stockService.addToWishlist(symbol);
    }

    // Remove from wishlist
    @DeleteMapping("/wishlist/{symbol}")
    public void removeFromWishlist(@PathVariable String symbol) {
        stockService.removeFromWishlist(symbol);
    }

    // View wishlist
    @GetMapping("/wishlist")
    public Iterable<Stock> getWishlist() {
        return stockService.getWishlist();
    }
}