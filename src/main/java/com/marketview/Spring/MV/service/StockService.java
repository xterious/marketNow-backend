package com.marketview.Spring.MV.service;

import com.marketview.Spring.MV.model.Stock;
import com.marketview.Spring.MV.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;

    public List<Stock> getAllStocks() {
        return stockRepository.findAll();
    }

    public Stock createStock(Stock stock) {
        return stockRepository.save(stock);
    }

    public Stock getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found: " + symbol));
    }

    public Stock updateStock(String symbol, Stock updatedStock) {
        Stock existing = getStockBySymbol(symbol);
        existing.setPrice(updatedStock.getPrice());
        existing.setVolume(updatedStock.getVolume());
        return stockRepository.save(existing);
    }
}

