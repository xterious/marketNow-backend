package com.market_view.spring.mv.service;

import com.market_view.spring.mv.model.Stock;
import com.market_view.spring.mv.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Cacheable(
            value = "stockSymbolsSearch",
            key = "#exchange + '_' + #query + '_' + #pageable.pageNumber + '_' + #pageable.pageSize"
    )
    public Page<Stock> getStockSymbols(String exchange, String query, Pageable pageable) {
        return stockRepository.findByMarketOriginAndStockSymbolContainingIgnoreCaseOrStockNameContainingIgnoreCase(
                exchange,
                query,
                query,
                pageable
        );
    }
}
