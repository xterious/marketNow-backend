package com.marketview.Spring.MV.service;

import com.marketview.Spring.MV.model.StockWishlist;
import com.marketview.Spring.MV.model.CurrencyWishlist;
import com.marketview.Spring.MV.model.NewsWishlist;
import com.marketview.Spring.MV.model.News;
import com.marketview.Spring.MV.repository.StockWishlistRepository;
import com.marketview.Spring.MV.repository.CurrencyWishlistRepository;
import com.marketview.Spring.MV.repository.NewsWishlistRepository;
import com.marketview.Spring.MV.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class WishlistService {

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);
    private final StockWishlistRepository stockWishlistRepository;
    private final CurrencyWishlistRepository currencyWishlistRepository;
    private final NewsWishlistRepository newsWishlistRepository;
    private final NewsRepository newsRepository;

    public WishlistService(StockWishlistRepository stockWishlistRepository,
                           CurrencyWishlistRepository currencyWishlistRepository,
                           NewsWishlistRepository newsWishlistRepository,
                           NewsRepository newsRepository) {
        this.stockWishlistRepository = stockWishlistRepository;
        this.currencyWishlistRepository = currencyWishlistRepository;
        this.newsWishlistRepository = newsWishlistRepository;
        this.newsRepository = newsRepository;
    }

    // Stock Wishlist Methods
    public StockWishlist getStockWishlist(String username) {
        return stockWishlistRepository.findByUsername(username)
                .orElseGet(() -> stockWishlistRepository.save(new StockWishlist(username)));
    }

    public StockWishlist addToStockWishlist(String username, String stockSymbol) {
        StockWishlist wishlist = getStockWishlist(username);
        wishlist.getFavoriteStocks().add(stockSymbol);
        logger.info("Added stock {} to wishlist for user {}", stockSymbol, username);
        return stockWishlistRepository.save(wishlist);
    }

    public StockWishlist removeFromStockWishlist(String username, String stockSymbol) {
        StockWishlist wishlist = getStockWishlist(username);
        wishlist.getFavoriteStocks().remove(stockSymbol);
        logger.info("Removed stock {} from wishlist for user {}", stockSymbol, username);
        return stockWishlistRepository.save(wishlist);
    }

    // Currency Wishlist Methods
    public CurrencyWishlist getCurrencyWishlist(String username) {
        return currencyWishlistRepository.findByUsername(username)
                .orElseGet(() -> currencyWishlistRepository.save(new CurrencyWishlist(username)));
    }

    public CurrencyWishlist addToCurrencyWishlist(String username, String currencyCode) {
        CurrencyWishlist wishlist = getCurrencyWishlist(username);
        wishlist.getFavoriteCurrencies().add(currencyCode);
        logger.info("Added currency {} to wishlist for user {}", currencyCode, username);
        return currencyWishlistRepository.save(wishlist);
    }

    public CurrencyWishlist removeFromCurrencyWishlist(String username, String currencyCode) {
        CurrencyWishlist wishlist = getCurrencyWishlist(username);
        wishlist.getFavoriteCurrencies().remove(currencyCode);
        logger.info("Removed currency {} from wishlist for user {}", currencyCode, username);
        return currencyWishlistRepository.save(wishlist);
    }

    // News Wishlist Methods
    public NewsWishlist getNewsWishlist(String username) {
        return newsWishlistRepository.findByUsername(username)
                .orElseGet(() -> newsWishlistRepository.save(new NewsWishlist(username)));
    }

    @Transactional
    public NewsWishlist addToNewsWishlist(String username, String newsIdentifier) {
        NewsWishlist wishlist = getNewsWishlist(username);
        wishlist.getFavoriteNews().add(newsIdentifier);
        
        // Use the enhanced repository method to find news
        Optional<News> existingNews = newsRepository.findByAnyIdentifier(newsIdentifier);
        if (existingNews.isPresent()) {
            logger.info("News article {} added to wishlist for user {}", newsIdentifier, username);
        } else {
            logger.warn("News article {} not found in database when adding to wishlist", newsIdentifier);
        }
        
        return newsWishlistRepository.save(wishlist);
    }

    public NewsWishlist removeFromNewsWishlist(String username, String newsIdentifier) {
        NewsWishlist wishlist = getNewsWishlist(username);
        wishlist.getFavoriteNews().remove(newsIdentifier);
        logger.info("News article {} removed from wishlist for user {}", newsIdentifier, username);
        return newsWishlistRepository.save(wishlist);
    }

    // Optimized method using repository queries
    public boolean isNewsInAnyWishlist(String newsIdentifier) {
        return newsWishlistRepository.countByFavoriteNewsContaining(newsIdentifier) > 0;
    }

    // Optimized method to get all wishlisted news identifiers
    public Set<String> getAllWishlistedNewsIds() {
        Set<String> allWishlistedNews = new HashSet<>();
        
        try {
            List<NewsWishlist> wishlists = newsWishlistRepository.findAllFavoriteNews();
            for (NewsWishlist wishlist : wishlists) {
                if (wishlist.getFavoriteNews() != null) {
                    allWishlistedNews.addAll(wishlist.getFavoriteNews());
                }
            }
            logger.debug("Found {} unique news items across all user wishlists", allWishlistedNews.size());
        } catch (Exception e) {
            logger.error("Error fetching wishlist data: {}", e.getMessage());
        }
        
        return allWishlistedNews;
    }

    // Optimized method to filter news not in wishlists
    public List<News> filterNewsNotInWishlists(List<News> newsList) {
        Set<String> wishlistedNewsIds = getAllWishlistedNewsIds();
        
        return newsList.stream()
                .filter(news -> !isNewsInWishlist(news, wishlistedNewsIds))
                .collect(Collectors.toList());
    }

    // Helper method to check if news is in wishlist
    private boolean isNewsInWishlist(News news, Set<String> wishlistedNewsIds) {
        String newsId = news.getNewsId();
        String newsUrl = news.getUrl();
        String newsHeadline = news.getHeadline();
        
        return wishlistedNewsIds.contains(newsId) || 
               wishlistedNewsIds.contains(newsUrl) || 
               wishlistedNewsIds.contains(newsHeadline);
    }

    // Utility methods for analytics
    public long getStockWishlistCount(String stockSymbol) {
        return stockWishlistRepository.countByFavoriteStocksContaining(stockSymbol);
    }

    public long getCurrencyWishlistCount(String currencyCode) {
        return currencyWishlistRepository.countByFavoriteCurrenciesContaining(currencyCode);
    }

    public long getNewsWishlistCount(String newsIdentifier) {
        return newsWishlistRepository.countByFavoriteNewsContaining(newsIdentifier);
    }
}