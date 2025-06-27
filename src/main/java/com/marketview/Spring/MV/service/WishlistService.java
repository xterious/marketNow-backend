package com.marketview.Spring.MV.service;

import com.marketview.Spring.MV.model.CurrencyWishlist;
import com.marketview.Spring.MV.model.NewsWishlist;
import com.marketview.Spring.MV.model.StockWishlist;
import com.marketview.Spring.MV.repository.CurrencyWishlistRepository;
import com.marketview.Spring.MV.repository.NewsWishlistRepository;
import com.marketview.Spring.MV.repository.StockWishlistRepository;
import com.marketview.Spring.MV.util.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class WishlistService {

    private static final Logger logger = LoggerFactory.getLogger(WishlistService.class);
    private final StockWishlistRepository stockWishlistRepository;
    private final CurrencyWishlistRepository currencyWishlistRepository;
    private final NewsWishlistRepository newsWishlistRepository;

    public WishlistService(StockWishlistRepository stockWishlistRepository,
                           CurrencyWishlistRepository currencyWishlistRepository,
                           NewsWishlistRepository newsWishlistRepository) {
        this.stockWishlistRepository = stockWishlistRepository;
        this.currencyWishlistRepository = currencyWishlistRepository;
        this.newsWishlistRepository = newsWishlistRepository;
    }

    // Stock Wishlist Methods
    public StockWishlist getStockWishlist(String username) {
        return stockWishlistRepository.findByUsername(username)
                .orElseGet(() -> stockWishlistRepository.save(new StockWishlist(username)));
    }

    public StockWishlist addToStockWishlist(String username, String stockSymbol) {
        StockWishlist wishlist = getStockWishlist(username);
        wishlist.getFavoriteStocks().add(stockSymbol);
        return stockWishlistRepository.save(wishlist);
    }

    public StockWishlist removeFromStockWishlist(String username, String stockSymbol) {
        StockWishlist wishlist = getStockWishlist(username);
        wishlist.getFavoriteStocks().remove(stockSymbol);
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
        return currencyWishlistRepository.save(wishlist);
    }

    public CurrencyWishlist removeFromCurrencyWishlist(String username, String currencyCode) {
        CurrencyWishlist wishlist = getCurrencyWishlist(username);
        wishlist.getFavoriteCurrencies().remove(currencyCode);
        return currencyWishlistRepository.save(wishlist);
    }

    // News Wishlist Methods
    public NewsWishlist getNewsWishlist(String username) {
        return newsWishlistRepository.findByUsername(username)
                .orElseGet(() -> newsWishlistRepository.save(new NewsWishlist(username)));
    }

    public NewsWishlist addToNewsWishlist(String username, String newsItem) {
        NewsWishlist wishlist = getNewsWishlist(username);
        wishlist.getFavoriteNews().add(newsItem);
        return newsWishlistRepository.save(wishlist);
    }

    public NewsWishlist removeFromNewsWishlist(String username, String newsItem) {
        NewsWishlist wishlist = getNewsWishlist(username);
        wishlist.getFavoriteNews().remove(newsItem);
        return newsWishlistRepository.save(wishlist);
    }
}