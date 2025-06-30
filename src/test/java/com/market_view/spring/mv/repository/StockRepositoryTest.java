package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@ActiveProfiles("test")
public class StockRepositoryTest {

    @Autowired
    private StockRepository stockRepository;

    private final Stock stock1 = new Stock();
    private final Stock stock2 = new Stock();
    private final Stock stock3 = new Stock();

    @BeforeEach
    public void setup() {
        stockRepository.deleteAll();

        stock1.setSymbol("AAPL");
        stock1.setCurrency("USD");
        stock1.setDescription("Apple Inc.");
        stock1.setDisplaySymbol("AAPL");
        stock1.setType("Common Stock");
        stock1.setExchange("NASDAQ");
        stock1.setCurrentPrice(190.00);
        stock1.setTimestamp(System.currentTimeMillis());

        stock2.setSymbol("GOOGL");
        stock2.setCurrency("USD");
        stock2.setDescription("Alphabet Inc.");
        stock2.setDisplaySymbol("GOOGL");
        stock2.setType("Common Stock");
        stock2.setExchange("NASDAQ");
        stock2.setCurrentPrice(2800.00);
        stock2.setTimestamp(System.currentTimeMillis());

        stock3.setSymbol("TSLA");
        stock3.setCurrency("USD");
        stock3.setDescription("Tesla Inc.");
        stock3.setDisplaySymbol("TSLA");
        stock3.setType("Common Stock");
        stock3.setExchange("NASDAQ");
        stock3.setCurrentPrice(150.00);
        stock3.setTimestamp(System.currentTimeMillis());

        stockRepository.saveAll(List.of(stock1, stock2, stock3));
    }

    @Test
    public void testFindBySymbol() {
        Optional<Stock> result = stockRepository.findBySymbol("AAPL");
        assertTrue(result.isPresent());
        assertEquals("Apple Inc.", result.get().getDescription());
    }

    @Test
    public void testFindByExchange() {
        List<Stock> nasdaqStocks = stockRepository.findByExchange("NASDAQ");
        assertEquals(3, nasdaqStocks.size());
    }

    @Test
    public void testFindByCurrency() {
        List<Stock> usdStocks = stockRepository.findByCurrency("USD");
        assertEquals(3, usdStocks.size());
    }

    @Test
    public void testFindByType() {
        List<Stock> commonStocks = stockRepository.findByType("Common Stock");
        assertEquals(3, commonStocks.size());
    }

    @Test
    public void testFindByCurrentPriceGreaterThan() {
        List<Stock> expensiveStocks = stockRepository.findByCurrentPriceGreaterThan(200.0);
        assertEquals(1, expensiveStocks.size());
        assertEquals("GOOGL", expensiveStocks.get(0).getSymbol());
    }

    @Test
    public void testFindByCurrentPriceLessThan() {
        List<Stock> cheapStocks = stockRepository.findByCurrentPriceLessThan(180.0);
        assertEquals(1, cheapStocks.size());
        assertEquals("TSLA", cheapStocks.get(0).getSymbol());
    }
}
