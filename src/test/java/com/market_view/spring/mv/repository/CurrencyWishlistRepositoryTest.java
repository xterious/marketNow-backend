package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.CurrencyWishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class CurrencyWishlistRepositoryTest {

    @Autowired
    private CurrencyWishlistRepository repository;

    private CurrencyWishlist wishlist1;
    private CurrencyWishlist wishlist2;

    @BeforeEach
    void setUp() {
        repository.deleteAll();

        wishlist1 = new CurrencyWishlist("user1", Set.of("USD", "EUR"));
        wishlist2 = new CurrencyWishlist("user2", Set.of("JPY", "INR"));

        repository.save(wishlist1);
        repository.save(wishlist2);
    }

    @Test
    void testFindByUsername() {
        Optional<CurrencyWishlist> result = repository.findByUsername("user1");
        assertThat(result).isPresent();
        assertThat(result.get().getFavoriteCurrencies()).contains("USD", "EUR");
    }

    @Test
    void testFindByFavoriteCurrenciesContaining() {
        List<CurrencyWishlist> result = repository.findByFavoriteCurrenciesContaining("USD");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("user1");
    }

    @Test
    void testFindAllCurrencyCodes() {
        List<CurrencyWishlist> result = repository.findAllCurrencyCodes();
        assertThat(result).hasSize(2);
        assertThat(result).anyMatch(w -> w.getFavoriteCurrencies().contains("USD"));
        assertThat(result).anyMatch(w -> w.getFavoriteCurrencies().contains("JPY"));
    }

    @Test
    void testCountByFavoriteCurrenciesContaining() {
        long count = repository.countByFavoriteCurrenciesContaining("INR");
        assertThat(count).isEqualTo(1);
    }
}
