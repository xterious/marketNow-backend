package com.market_view.spring.mv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market_view.spring.mv.model.Stock;
import com.market_view.spring.mv.repository.StockRepository;
import com.market_view.spring.mv.util.CustomException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceTest {

    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;
    @Mock private StockRepository stockRepository;

    @InjectMocks private StockService stockService;

    private String sampleQuoteJson;
    private String sampleSymbolsJson;

    @BeforeEach
    void setup() {
        sampleQuoteJson = """
            {
              "c": 150.25
            }
        """;

        sampleSymbolsJson = """
            [
              { "symbol": "AAPL" },
              { "symbol": "GOOGL" },
              { "symbol": "MSFT" }
            ]
        """;
    }

    @Test
    void testGetStockQuote_Success() throws Exception {
        JsonNode mockJson = new ObjectMapper().readTree(sampleQuoteJson);
        when(restTemplate.getForEntity(contains("quote?symbol=AAPL"), eq(String.class)))
                .thenReturn(new ResponseEntity<>(sampleQuoteJson, HttpStatus.OK));
        when(objectMapper.readTree(sampleQuoteJson)).thenReturn(mockJson);

        Stock stock = stockService.getStockQuote("AAPL");

        assertNotNull(stock);
        assertEquals("AAPL", stock.getSymbol());
        assertEquals(150.25, stock.getCurrentPrice());
    }

    @Test
    void testGetStockQuote_Failure_ThrowsException() throws Exception {
        when(restTemplate.getForEntity(anyString(), eq(String.class)))
                .thenThrow(new RuntimeException("API error"));

        Exception ex = assertThrows(CustomException.class, () -> stockService.getStockQuote("AAPL"));
        assertTrue(ex.getMessage().contains("Error fetching quote for AAPL"));
    }

    @Test
    void testGetStockSymbols_FetchesAndLimitsTo10() throws Exception {
        JsonNode mockSymbols = new ObjectMapper().readTree(sampleSymbolsJson);
        JsonNode mockQuote = new ObjectMapper().readTree(sampleQuoteJson);

        when(restTemplate.getForEntity(contains("stock/symbol?exchange=NSE"), eq(String.class)))
                .thenReturn(new ResponseEntity<>(sampleSymbolsJson, HttpStatus.OK));
        when(objectMapper.readTree(sampleSymbolsJson)).thenReturn(mockSymbols);
        when(restTemplate.getForEntity(contains("quote?symbol="), eq(String.class)))
                .thenReturn(new ResponseEntity<>(sampleQuoteJson, HttpStatus.OK));
        when(objectMapper.readTree(sampleQuoteJson)).thenReturn(mockQuote);

        List<Stock> result = stockService.getStockSymbols("NSE");

        assertNotNull(result);
        assertEquals(3, result.size());
        assertEquals("AAPL", result.get(0).getSymbol());
    }

    @Test
    void testUpdateStockDataInDatabase_UpdatesStocks() throws Exception {
        Stock existingStock = new Stock();
        existingStock.setSymbol("AAPL");

        JsonNode mockQuote = new ObjectMapper().readTree(sampleQuoteJson);
        when(stockRepository.findAll()).thenReturn(List.of(existingStock));
        when(restTemplate.getForEntity(contains("quote?symbol=AAPL"), eq(String.class)))
                .thenReturn(new ResponseEntity<>(sampleQuoteJson, HttpStatus.OK));
        when(objectMapper.readTree(sampleQuoteJson)).thenReturn(mockQuote);

        stockService.updateStockDataInDatabase();

        verify(stockRepository, times(1)).save(any(Stock.class));
    }

    @Test
    void testClearStockCache_NoErrors() {
        assertDoesNotThrow(() -> stockService.clearStockCache());
    }
}
