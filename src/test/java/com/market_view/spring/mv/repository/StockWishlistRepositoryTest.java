package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.StockWishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class StockWishlistRepositoryTest {

    @Autowired
    private StockWishlistRepository repository;

    private final String username1 = "user1";
    private final String username2 = "user2";

    @BeforeEach
    public void setup() {
        repository.deleteAll();

        StockWishlist wishlist1 = new StockWishlist(username1);
        wishlist1.getFavoriteStocks().add("AAPL");
        wishlist1.getFavoriteStocks().add("GOOGL");

        StockWishlist wishlist2 = new StockWishlist(username2);
        wishlist2.getFavoriteStocks().add("MSFT");
        wishlist2.getFavoriteStocks().add("AAPL"); // common symbol

        repository.saveAll(List.of(wishlist1, wishlist2));
    }

    @Test
    public void testFindByUsername() {
        Optional<StockWishlist> result = repository.findByUsername(username1);
        assertTrue(result.isPresent());
        assertEquals(username1, result.get().getUsername());
        assertTrue(result.get().getFavoriteStocks().contains("AAPL"));
    }

    @Test
    public void testFindByFavoriteStocksContaining() {
        List<StockWishlist> result = repository.findByFavoriteStocksContaining("AAPL");
        assertEquals(2, result.size());
    }

    @Test
    public void testCountByFavoriteStocksContaining() {
        long count = repository.countByFavoriteStocksContaining("AAPL");
        assertEquals(2, count);
    }

    @Test
    public void testFindAllStockSymbols() {
        List<StockWishlist> wishlists = repository.findAllStockSymbols();
        Set<String> symbols = wishlists.stream()
                .flatMap(w -> w.getFavoriteStocks().stream())
                .collect(java.util.stream.Collectors.toSet());

        assertTrue(symbols.contains("AAPL"));
        assertTrue(symbols.contains("GOOGL"));
        assertTrue(symbols.contains("MSFT"));
    }
}
