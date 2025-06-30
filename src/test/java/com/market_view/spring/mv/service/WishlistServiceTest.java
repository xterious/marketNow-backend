package com.market_view.spring.mv.service;

import com.market_view.spring.mv.model.CurrencyWishlist;
import com.market_view.spring.mv.model.News;
import com.market_view.spring.mv.model.NewsWishlist;
import com.market_view.spring.mv.model.StockWishlist;
import com.market_view.spring.mv.repository.CurrencyWishlistRepository;
import com.market_view.spring.mv.repository.NewsRepository;
import com.market_view.spring.mv.repository.NewsWishlistRepository;
import com.market_view.spring.mv.repository.StockWishlistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {

    @Mock
    private StockWishlistRepository stockWishlistRepository;

    @Mock
    private CurrencyWishlistRepository currencyWishlistRepository;

    @Mock
    private NewsWishlistRepository newsWishlistRepository;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private WishlistService wishlistService;

    private final String username = "testuser";

    private StockWishlist stockWishlist;
    private CurrencyWishlist currencyWishlist;
    private NewsWishlist newsWishlist;

    @BeforeEach
    void setUp() {
        stockWishlist = new StockWishlist(username);
        stockWishlist.setFavoriteStocks(new HashSet<>());

        currencyWishlist = new CurrencyWishlist(username);
        currencyWishlist.setFavoriteCurrencies(new HashSet<>());

        newsWishlist = new NewsWishlist(username);
        newsWishlist.setFavoriteNews(new HashSet<>());
    }

    // Stock Wishlist Tests

    @Test
    void testGetStockWishlist_CreateNew() {
        when(stockWishlistRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(stockWishlistRepository.save(any(StockWishlist.class))).thenReturn(stockWishlist);

        StockWishlist result = wishlistService.getStockWishlist(username);

        assertNotNull(result);
        verify(stockWishlistRepository).save(any(StockWishlist.class));
    }

    @Test
    void testAddToStockWishlist() {
        stockWishlist.getFavoriteStocks().add("AAPL");
        when(stockWishlistRepository.findByUsername(username)).thenReturn(Optional.of(stockWishlist));
        when(stockWishlistRepository.save(stockWishlist)).thenReturn(stockWishlist);

        StockWishlist result = wishlistService.addToStockWishlist(username, "GOOGL");

        assertTrue(result.getFavoriteStocks().contains("AAPL"));
        assertTrue(result.getFavoriteStocks().contains("GOOGL"));
        verify(stockWishlistRepository).save(stockWishlist);
    }

    @Test
    void testRemoveFromStockWishlist() {
        stockWishlist.getFavoriteStocks().add("AAPL");
        when(stockWishlistRepository.findByUsername(username)).thenReturn(Optional.of(stockWishlist));
        when(stockWishlistRepository.save(stockWishlist)).thenReturn(stockWishlist);

        StockWishlist result = wishlistService.removeFromStockWishlist(username, "AAPL");

        assertFalse(result.getFavoriteStocks().contains("AAPL"));
        verify(stockWishlistRepository).save(stockWishlist);
    }

    // Currency Wishlist Tests

    @Test
    void testGetCurrencyWishlist_CreateNew() {
        when(currencyWishlistRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(currencyWishlistRepository.save(any(CurrencyWishlist.class))).thenReturn(currencyWishlist);

        CurrencyWishlist result = wishlistService.getCurrencyWishlist(username);

        assertNotNull(result);
        verify(currencyWishlistRepository).save(any(CurrencyWishlist.class));
    }

    @Test
    void testAddToCurrencyWishlist() {
        currencyWishlist.getFavoriteCurrencies().add("USD");
        when(currencyWishlistRepository.findByUsername(username)).thenReturn(Optional.of(currencyWishlist));
        when(currencyWishlistRepository.save(currencyWishlist)).thenReturn(currencyWishlist);

        CurrencyWishlist result = wishlistService.addToCurrencyWishlist(username, "EUR");

        assertTrue(result.getFavoriteCurrencies().contains("USD"));
        assertTrue(result.getFavoriteCurrencies().contains("EUR"));
        verify(currencyWishlistRepository).save(currencyWishlist);
    }

    @Test
    void testRemoveFromCurrencyWishlist() {
        currencyWishlist.getFavoriteCurrencies().add("USD");
        when(currencyWishlistRepository.findByUsername(username)).thenReturn(Optional.of(currencyWishlist));
        when(currencyWishlistRepository.save(currencyWishlist)).thenReturn(currencyWishlist);

        CurrencyWishlist result = wishlistService.removeFromCurrencyWishlist(username, "USD");

        assertFalse(result.getFavoriteCurrencies().contains("USD"));
        verify(currencyWishlistRepository).save(currencyWishlist);
    }

    // News Wishlist Tests

    @Test
    void testGetNewsWishlist_CreateNew() {
        when(newsWishlistRepository.findByUsername(username)).thenReturn(Optional.empty());
        when(newsWishlistRepository.save(any(NewsWishlist.class))).thenReturn(newsWishlist);

        NewsWishlist result = wishlistService.getNewsWishlist(username);

        assertNotNull(result);
        verify(newsWishlistRepository).save(any(NewsWishlist.class));
    }

    @Test
    void testAddToNewsWishlist_FoundInNewsRepo() {
        String newsId = "news123";
        newsWishlist.getFavoriteNews().add("existingNews");

        when(newsWishlistRepository.findByUsername(username)).thenReturn(Optional.of(newsWishlist));
        when(newsWishlistRepository.save(newsWishlist)).thenReturn(newsWishlist);
        when(newsRepository.findByAnyIdentifier(newsId)).thenReturn(Optional.of(new News()));

        NewsWishlist result = wishlistService.addToNewsWishlist(username, newsId);

        assertTrue(result.getFavoriteNews().contains(newsId));
        verify(newsWishlistRepository).save(newsWishlist);
    }

    @Test
    void testAddToNewsWishlist_NotFoundInNewsRepo() {
        String newsId = "missingNews";

        when(newsWishlistRepository.findByUsername(username)).thenReturn(Optional.of(newsWishlist));
        when(newsWishlistRepository.save(newsWishlist)).thenReturn(newsWishlist);
        when(newsRepository.findByAnyIdentifier(newsId)).thenReturn(Optional.empty());

        NewsWishlist result = wishlistService.addToNewsWishlist(username, newsId);

        assertTrue(result.getFavoriteNews().contains(newsId));
        verify(newsWishlistRepository).save(newsWishlist);
    }

    @Test
    void testRemoveFromNewsWishlist() {
        newsWishlist.getFavoriteNews().add("news123");
        when(newsWishlistRepository.findByUsername(username)).thenReturn(Optional.of(newsWishlist));
        when(newsWishlistRepository.save(newsWishlist)).thenReturn(newsWishlist);

        NewsWishlist result = wishlistService.removeFromNewsWishlist(username, "news123");

        assertFalse(result.getFavoriteNews().contains("news123"));
        verify(newsWishlistRepository).save(newsWishlist);
    }

    // Test isNewsInAnyWishlist

    @Test
    void testIsNewsInAnyWishlist_True() {
        when(newsWishlistRepository.countByFavoriteNewsContaining("news1")).thenReturn(1L);

        assertTrue(wishlistService.isNewsInAnyWishlist("news1"));
    }

    @Test
    void testIsNewsInAnyWishlist_False() {
        when(newsWishlistRepository.countByFavoriteNewsContaining("news2")).thenReturn(0L);

        assertFalse(wishlistService.isNewsInAnyWishlist("news2"));
    }

    // Test getAllWishlistedNewsIds and filterNewsNotInWishlists

    @Test
    void testGetAllWishlistedNewsIds() {
        NewsWishlist w1 = new NewsWishlist("user1");
        w1.setFavoriteNews(Set.of("newsA", "newsB"));

        NewsWishlist w2 = new NewsWishlist("user2");
        w2.setFavoriteNews(Set.of("newsB", "newsC"));

        when(newsWishlistRepository.findAllFavoriteNews()).thenReturn(List.of(w1, w2));

        Set<String> allIds = wishlistService.getAllWishlistedNewsIds();

        assertEquals(3, allIds.size());
        assertTrue(allIds.containsAll(List.of("newsA", "newsB", "newsC")));
    }

    @Test
    void testFilterNewsNotInWishlists() {
        News news1 = new News();
        news1.setNewsId("newsA");
        news1.setUrl("url1");
        news1.setHeadline("headline1");

        News news2 = new News();
        news2.setNewsId("newsX");
        news2.setUrl("urlX");
        news2.setHeadline("headlineX");

        Set<String> wishlistedIds = Set.of("newsA", "urlX", "headlineY");

        List<News> inputList = List.of(news1, news2);

        // spy the wishlistService to mock getAllWishlistedNewsIds() to return wishlistedIds
        WishlistService spyService = spy(wishlistService);
        doReturn(wishlistedIds).when(spyService).getAllWishlistedNewsIds();

        List<News> filtered = spyService.filterNewsNotInWishlists(inputList);

        // news1 is wishlisted by id, news2 has url in wishlisted so it should be filtered out as well
        assertTrue(filtered.isEmpty());
    }
}
