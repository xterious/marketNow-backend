package com.market_view.spring.mv.service;

import com.market_view.spring.mv.model.Market;
import com.market_view.spring.mv.repository.MarketRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class MarketService {


    private final MarketRepository marketRepository;

    public List<Market> getAllMarkets() {
        return marketRepository.findAll();
    }

    public Market getMarketById(String id) {
        return marketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Market not found"));
    }

    public Market createMarket(Market market) {
        return marketRepository.save(market);
    }

    public Market updateMarket(String id, Market marketDetails) {
        Market market = getMarketById(id);
        market.setName(marketDetails.getName());
        market.setDescription(marketDetails.getDescription());
        // update other fields as needed
        return marketRepository.save(market);
    }

    public void deleteMarket(String id) {
        Market market = getMarketById(id);
        marketRepository.delete(market);
    }
}
