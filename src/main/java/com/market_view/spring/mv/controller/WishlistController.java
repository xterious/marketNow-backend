package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.CurrencyWishlist;
import com.market_view.spring.mv.model.NewsWishlist;
import com.market_view.spring.mv.model.StockWishlist;
import com.market_view.spring.mv.service.WishlistService;
import com.market_view.spring.mv.util.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    // Stock Wishlist Endpoints
    @GetMapping("/stock/{username}")
    public ResponseEntity<StockWishlist> getStockWishlist(@PathVariable String username, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.getStockWishlist(username));
    }

    @PostMapping("/stock/{username}/{stockSymbol}")
    public ResponseEntity<StockWishlist> addToStockWishlist(@PathVariable String username, @PathVariable String stockSymbol, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.addToStockWishlist(username, stockSymbol));
    }

    @DeleteMapping("/stock/{username}/{stockSymbol}")
    public ResponseEntity<StockWishlist> removeFromStockWishlist(@PathVariable String username, @PathVariable String stockSymbol, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.removeFromStockWishlist(username, stockSymbol));
    }

    // Currency Wishlist Endpoints
    @GetMapping("/currency/{username}")
    public ResponseEntity<CurrencyWishlist> getCurrencyWishlist(@PathVariable String username, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.getCurrencyWishlist(username));
    }

    @PostMapping("/currency/{username}/{currencyCode}")
    public ResponseEntity<CurrencyWishlist> addToCurrencyWishlist(@PathVariable String username, @PathVariable String currencyCode, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.addToCurrencyWishlist(username, currencyCode));
    }

    @DeleteMapping("/currency/{username}/{currencyCode}")
    public ResponseEntity<CurrencyWishlist> removeFromCurrencyWishlist(@PathVariable String username, @PathVariable String currencyCode, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.removeFromCurrencyWishlist(username, currencyCode));
    }

    // News Wishlist Endpoints
    @GetMapping("/news/{username}")
    public ResponseEntity<NewsWishlist> getNewsWishlist(@PathVariable String username, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.getNewsWishlist(username));
    }

    @PostMapping("/news/{username}/{newsItem}")
    public ResponseEntity<NewsWishlist> addToNewsWishlist(@PathVariable String username, @PathVariable String newsItem, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.addToNewsWishlist(username, newsItem));
    }

    @DeleteMapping("/news/{username}/{newsItem}")
    public ResponseEntity<NewsWishlist> removeFromNewsWishlist(@PathVariable String username, @PathVariable String newsItem, Authentication auth) {
        validateUser(auth, username);
        return ResponseEntity.ok(wishlistService.removeFromNewsWishlist(username, newsItem));
    }

    private void validateUser(Authentication auth, String username) {
        if (auth == null || !auth.getName().equals(username)) {
            throw new CustomException("Unauthorized access to wishlist", org.springframework.http.HttpStatus.FORBIDDEN);
        }
    }
}