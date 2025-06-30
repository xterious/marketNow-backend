package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.Market;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class MarketRepositoryTest {

    @Autowired
    private MarketRepository marketRepository;

    private Market nasdaq;
    private Market nyse;

    @BeforeEach
    public void setUp() {
        marketRepository.deleteAll();

        nasdaq = new Market("NASDAQ", "Tech-heavy stock market");
        nyse = new Market("NYSE", "Largest stock exchange in the world");

        marketRepository.save(nasdaq);
        marketRepository.save(nyse);
    }

    @Test
    public void testSaveAndFindById() {
        Optional<Market> result = marketRepository.findById(nasdaq.getId());
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("NASDAQ");
    }

    @Test
    public void testFindAll() {
        List<Market> markets = marketRepository.findAll();
        assertThat(markets).hasSize(2);
        assertThat(markets).extracting("name").containsExactlyInAnyOrder("NASDAQ", "NYSE");
    }

    @Test
    public void testDeleteById() {
        marketRepository.deleteById(nasdaq.getId());
        assertThat(marketRepository.findById(nasdaq.getId())).isNotPresent();
        assertThat(marketRepository.findAll()).hasSize(1);
    }

    @Test
    public void testUpdateMarket() {
        nyse.setDescription("Updated description");
        marketRepository.save(nyse);

        Optional<Market> updated = marketRepository.findById(nyse.getId());
        assertThat(updated).isPresent();
        assertThat(updated.get().getDescription()).isEqualTo("Updated description");
    }
}
