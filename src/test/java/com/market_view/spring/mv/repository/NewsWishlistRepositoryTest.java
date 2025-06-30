package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.NewsWishlist;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
public class NewsWishlistRepositoryTest {

    @Autowired
    private NewsWishlistRepository newsWishlistRepository;

    @BeforeEach
    public void setUp() {
        newsWishlistRepository.deleteAll(); // Clean the collection
    }

    @Test
    public void testFindByUsername() {
        NewsWishlist wishlist = new NewsWishlist("user1", new HashSet<>(Set.of("news1", "news2")));
        newsWishlistRepository.save(wishlist);

        Optional<NewsWishlist> found = newsWishlistRepository.findByUsername("user1");
        assertTrue(found.isPresent());
        assertEquals(Set.of("news1", "news2"), found.get().getFavoriteNews());
    }

    @Test
    public void testFindByFavoriteNewsContaining() {
        NewsWishlist wishlist = new NewsWishlist("user2", new HashSet<>(Set.of("news3", "news4")));
        newsWishlistRepository.save(wishlist);

        List<NewsWishlist> result = newsWishlistRepository.findByFavoriteNewsContaining("news4");
        assertEquals(1, result.size());
        assertEquals("user2", result.get(0).getUsername());
    }

    @Test
    public void testCountByFavoriteNewsContaining() {
        newsWishlistRepository.save(new NewsWishlist("user3", new HashSet<>(Set.of("newsX", "newsY"))));
        newsWishlistRepository.save(new NewsWishlist("user4", new HashSet<>(Set.of("newsY"))));

        long count = newsWishlistRepository.countByFavoriteNewsContaining("newsY");
        assertEquals(2, count);
    }

    @Test
    public void testFindByFavoriteNewsContainingAny() {
        Set<String> targetNews = Set.of("news10", "news11");

        newsWishlistRepository.save(new NewsWishlist("user5", new HashSet<>(Set.of("news10", "news12"))));
        newsWishlistRepository.save(new NewsWishlist("user6", new HashSet<>(Set.of("news11"))));

        List<NewsWishlist> results = newsWishlistRepository.findByFavoriteNewsContainingAny(targetNews);
        assertEquals(2, results.size());
    }

    @Test
    public void testFindAllFavoriteNews() {
        newsWishlistRepository.save(new NewsWishlist("user7", new HashSet<>(Set.of("news100", "news101"))));
        newsWishlistRepository.save(new NewsWishlist("user8", new HashSet<>(Set.of("news102"))));

        List<NewsWishlist> favorites = newsWishlistRepository.findAllFavoriteNews();
        Set<String> flattened = new HashSet<>();
        for (NewsWishlist wl : favorites) {
            flattened.addAll(wl.getFavoriteNews());
        }

        assertTrue(flattened.contains("news100"));
        assertTrue(flattened.contains("news101"));
        assertTrue(flattened.contains("news102"));
    }
}
