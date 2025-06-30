package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.CurrencyExchangeRate;
import com.market_view.spring.mv.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CurrencyControllerTest {

    @Mock
    private CurrencyService currencyService;

    @InjectMocks
    private CurrencyController currencyController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetExchangeRate_Valid() {
        String base = "USD";
        String target = "EUR";
        String customerType = "normal";

        CurrencyExchangeRate rate = new CurrencyExchangeRate(base, target, BigDecimal.ONE, BigDecimal.ONE, customerType);
        when(currencyService.isValidCurrencyPair(base, target)).thenReturn(true);
        when(currencyService.getExchangeRate(base, target, customerType)).thenReturn(rate);

        ResponseEntity<CurrencyExchangeRate> response = currencyController.getExchangeRate(base, target, customerType);

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(base, response.getBody().getBase());
        verify(currencyService).getExchangeRate(base, target, customerType);
    }

    @Test
    void testGetExchangeRate_InvalidCustomerType() {
        ResponseEntity<CurrencyExchangeRate> response = currencyController.getExchangeRate("USD", "EUR", "invalidType");
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(currencyService, never()).getExchangeRate(anyString(), anyString(), anyString());
    }

    @Test
    void testGetExchangeRate_InvalidCurrencyPair() {
        String base = "USD";
        String target = "XYZ";
        String customerType = "normal";

        when(currencyService.isValidCurrencyPair(base, target)).thenReturn(false);

        ResponseEntity<CurrencyExchangeRate> response = currencyController.getExchangeRate(base, target, customerType);

        assertEquals(404, response.getStatusCodeValue());
        verify(currencyService, never()).getExchangeRate(anyString(), anyString(), anyString());
    }

    @Test
    void testConvertCurrency_Valid() {
        String base = "USD";
        String target = "EUR";
        BigDecimal amount = new BigDecimal("100");
        String customerType = "special";

        BigDecimal convertedAmount = new BigDecimal("85.00");

        when(currencyService.isValidCurrencyPair(base, target)).thenReturn(true);
        when(currencyService.convertCurrency(base, target, amount, customerType)).thenReturn(convertedAmount);

        ResponseEntity<BigDecimal> response = currencyController.convertCurrency(base, target, amount, customerType);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(convertedAmount, response.getBody());
        verify(currencyService).convertCurrency(base, target, amount, customerType);
    }

    @Test
    void testConvertCurrency_InvalidCustomerType() {
        ResponseEntity<BigDecimal> response = currencyController.convertCurrency("USD", "EUR", BigDecimal.TEN, "wrongType");
        assertEquals(400, response.getStatusCodeValue());
        assertNull(response.getBody());
        verify(currencyService, never()).convertCurrency(anyString(), anyString(), any(), anyString());
    }

    @Test
    void testConvertCurrency_InvalidCurrencyPair() {
        String base = "USD";
        String target = "AAA";
        BigDecimal amount = BigDecimal.ONE;
        String customerType = "normal";

        when(currencyService.isValidCurrencyPair(base, target)).thenReturn(false);

        ResponseEntity<BigDecimal> response = currencyController.convertCurrency(base, target, amount, customerType);

        assertEquals(404, response.getStatusCodeValue());
        verify(currencyService, never()).convertCurrency(anyString(), anyString(), any(), anyString());
    }

    @Test
    void testGetExchangeRate_ExceptionHandling() {
        String base = "USD";
        String target = "EUR";
        String customerType = "normal";

        when(currencyService.isValidCurrencyPair(base, target)).thenThrow(new RuntimeException("API failure"));

        ResponseEntity<CurrencyExchangeRate> response = currencyController.getExchangeRate(base, target, customerType);

        assertEquals(500, response.getStatusCodeValue());
    }

    @Test
    void testConvertCurrency_ExceptionHandling() {
        String base = "USD";
        String target = "EUR";
        BigDecimal amount = BigDecimal.ONE;
        String customerType = "normal";

        when(currencyService.isValidCurrencyPair(base, target)).thenReturn(true);
        when(currencyService.convertCurrency(base, target, amount, customerType))
                .thenThrow(new RuntimeException("Conversion failed"));

        ResponseEntity<BigDecimal> response = currencyController.convertCurrency(base, target, amount, customerType);

        assertEquals(500, response.getStatusCodeValue());
    }
}
