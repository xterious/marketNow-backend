package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.StockWishlist;
import com.market_view.spring.mv.service.WishlistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WishlistController.class)
@AutoConfigureMockMvc(addFilters = false) // disable security filters for testing
public class WishlistControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WishlistService wishlistService;

    private Authentication auth;

    private final String username = "testuser";
    private final String stockSymbol = "AAPL";

    @BeforeEach
    public void setup() {
        // Mock Authentication with correct username
        auth = Mockito.mock(Authentication.class);
        when(auth.getName()).thenReturn(username);
    }

    @Test
    public void getStockWishlist_Success() throws Exception {
        StockWishlist mockWishlist = new StockWishlist();
        mockWishlist.setUsername(username);
        mockWishlist.setFavoriteStocks(java.util.Set.of(stockSymbol));  // <-- here

        when(wishlistService.getStockWishlist(username)).thenReturn(mockWishlist);

        mockMvc.perform(get("/api/wishlist/stock/{username}", username)
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.favoriteStocks[0]").value(stockSymbol)); // also note this JSON path change
    }

    @Test
    public void getStockWishlist_Unauthorized() throws Exception {
        Authentication badAuth = Mockito.mock(Authentication.class);
        when(badAuth.getName()).thenReturn("otheruser");

        mockMvc.perform(get("/api/wishlist/stock/{username}", username)
                        .principal(badAuth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    public void addToStockWishlist_Success() throws Exception {
        StockWishlist updatedWishlist = new StockWishlist();
        updatedWishlist.setUsername(username);
        updatedWishlist.setFavoriteStocks(java.util.Set.of(stockSymbol));  // <-- here

        when(wishlistService.addToStockWishlist(username, stockSymbol)).thenReturn(updatedWishlist);

        mockMvc.perform(post("/api/wishlist/stock/{username}/{stockSymbol}", username, stockSymbol)
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.favoriteStocks[0]").value(stockSymbol)); // JSON path update
    }

    @Test
    public void removeFromStockWishlist_Success() throws Exception {
        StockWishlist updatedWishlist = new StockWishlist();
        updatedWishlist.setUsername(username);
        updatedWishlist.setFavoriteStocks(java.util.Set.of()); // empty set after removal

        when(wishlistService.removeFromStockWishlist(username, stockSymbol)).thenReturn(updatedWishlist);

        mockMvc.perform(delete("/api/wishlist/stock/{username}/{stockSymbol}", username, stockSymbol)
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.favoriteStocks").isEmpty()); // JSON path update
    }

    @Test
    public void addToStockWishlist_Unauthorized() throws Exception {
        Authentication badAuth = Mockito.mock(Authentication.class);
        when(badAuth.getName()).thenReturn("otheruser");

        mockMvc.perform(post("/api/wishlist/stock/{username}/{stockSymbol}", username, stockSymbol)
                        .principal(badAuth)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
