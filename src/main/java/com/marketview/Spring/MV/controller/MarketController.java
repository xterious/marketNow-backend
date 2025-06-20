package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Market;
import com.marketview.Spring.MV.service.MarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/markets")
public class MarketController {

    @Autowired
    private MarketService marketService;

    @GetMapping
    public List<Market> getAllMarkets() {
        return marketService.getAllMarkets();
    }

    @GetMapping("/{id}")
    public Market getMarketById(@PathVariable String id) {
        return marketService.getMarketById(id);
    }

    @PostMapping
    public Market createMarket(@RequestBody Market market) {
        return marketService.createMarket(market);
    }

    @PutMapping("/{id}")
    public Market updateMarket(@PathVariable String id, @RequestBody Market market) {
        return marketService.updateMarket(id, market);
    }

    @DeleteMapping("/{id}")
    public void deleteMarket(@PathVariable String id) {
        marketService.deleteMarket(id);
    }
}
