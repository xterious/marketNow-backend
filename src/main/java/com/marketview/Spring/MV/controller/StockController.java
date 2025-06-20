package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stocks")
public class StockController {

    @Autowired
    private StockService stockService;

    @GetMapping
    public List<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }

    @PostMapping
    public Stock createStock(@RequestBody Stock stock) {
        return stockService.createStock(stock);
    }

    @GetMapping("/{symbol}")
    public Stock getStockBySymbol(@PathVariable String symbol) {
        return stockService.getStockBySymbol(symbol);
    }

    @PutMapping("/{symbol}")
    public Stock updateStock(@PathVariable String symbol, @RequestBody Stock stock) {
        return stockService.updateStock(symbol, stock);
    }


}

