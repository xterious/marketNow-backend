package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.Stock;
import com.market_view.spring.mv.service.StockService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(StockController.class)
public class StockControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StockService stockService;

    @MockBean
    private SimpMessagingTemplate messagingTemplate;

    @Test
    void getStockSymbols_ShouldReturnPaginatedList() throws Exception {
        Stock stock1 = new Stock();
        stock1.setSymbol("AAPL");
        stock1.setCurrency("USD");
        stock1.setDescription("Apple Inc.");

        Stock stock2 = new Stock();
        stock2.setSymbol("GOOG");
        stock2.setCurrency("USD");
        stock2.setDescription("Google LLC");

        List<Stock> mockStocks = List.of(stock1, stock2);

        Mockito.when(stockService.getStockSymbols("US")).thenReturn(mockStocks);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/symbols?exchange=US&page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].symbol", is("AAPL")))
                .andExpect(jsonPath("$[1].symbol", is("GOOG")))
                .andDo(print());
    }

    @Test
    void getStockQuote_ShouldReturnStock() throws Exception {
        Stock stock = new Stock();
        stock.setSymbol("AAPL");
        stock.setCurrency("USD");
        stock.setDescription("Apple Inc.");
        stock.setCurrentPrice(180.55);
        stock.setPercentChange(1.2);
        stock.setHigh(185.00);
        stock.setLow(179.00);
        stock.setTimestamp(System.currentTimeMillis());

        Mockito.when(stockService.getStockQuote("AAPL")).thenReturn(stock);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/stocks/quote?symbol=AAPL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.symbol", is("AAPL")))
                .andExpect(jsonPath("$.description", is("Apple Inc.")))
                .andExpect(jsonPath("$.currentPrice", is(180.55)))
                .andDo(print());
    }
}
