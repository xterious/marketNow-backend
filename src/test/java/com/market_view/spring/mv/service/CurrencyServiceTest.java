package com.market_view.spring.mv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market_view.spring.mv.model.CurrencyExchangeRate;
import com.market_view.spring.mv.repository.CurrencyExchangeRateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceTest {

    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;
    @Mock private CurrencyExchangeRateRepository currencyExchangeRateRepository;

    @InjectMocks private CurrencyService currencyService;

    private final String base = "USD";
    private final String target = "INR";
    private final String customerType = "standard";

    @BeforeEach
    void setupFixerKey() {
        // Set fake API key manually (if needed)
        // ReflectionTestUtils.setField(currencyService, "fixerApiKey", "fake-key");
    }

    @Test
    void testGetExchangeRate_FromDatabase() {
        CurrencyExchangeRate mockRate = new CurrencyExchangeRate(base, target, new BigDecimal("83.123456"),
                new BigDecimal("83.539073"), customerType);

        when(currencyExchangeRateRepository.findByBaseAndTargetAndCustomerType(base, target, customerType))
                .thenReturn(Optional.of(mockRate));

        CurrencyExchangeRate rate = currencyService.getExchangeRate(base, target, customerType);
        assertNotNull(rate);
        assertEquals("USD", rate.getBase());
        verify(currencyExchangeRateRepository, never()).save(any());
    }

    @Test
    void testConvertCurrency_MultipliesCorrectly() {
        CurrencyExchangeRate rate = new CurrencyExchangeRate(base, target,
                new BigDecimal("83.000000"), new BigDecimal("85.000000"), customerType);

        when(currencyExchangeRateRepository.findByBaseAndTargetAndCustomerType(base, target, customerType))
                .thenReturn(Optional.of(rate));

        BigDecimal amount = new BigDecimal("10");
        BigDecimal result = currencyService.convertCurrency(base, target, amount, customerType);

        assertEquals(new BigDecimal("850.0000"), result);
    }

    @Test
    void testIsValidCurrencyPair_ValidPair() throws Exception {
        String apiResponse = """
            {
              "success": true,
              "rates": {
                "INR": "83.123456"
              }
            }
        """;

        when(restTemplate.getForObject(anyString(), eq(String.class))).thenReturn(apiResponse);
        JsonNode jsonNode = new ObjectMapper().readTree(apiResponse);
        when(objectMapper.readTree(apiResponse)).thenReturn(jsonNode);

        boolean valid = currencyService.isValidCurrencyPair("USD", "INR");
        assertTrue(valid);
    }

    @Test
    void testIsValidCurrencyPair_InvalidCurrency() {
        boolean valid = currencyService.isValidCurrencyPair("XYZ", "INR");
        assertFalse(valid);
    }

    @Test
    void testSetLiborSpreadNormal() {
        currencyService.setLiborSpreadNormal(new BigDecimal("0.006"));
        assertEquals(new BigDecimal("0.006000"), currencyService.getLiborSpreadNormal());
    }

    @Test
    void testSetLiborSpreadSpecial() {
        currencyService.setLiborSpreadSpecial(new BigDecimal("0.003"));
        assertEquals(new BigDecimal("0.003000"), currencyService.getLiborSpreadSpecial());
    }

    @Test
    void testSetLiborSpreadSpecial_InvalidNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> currencyService.setLiborSpreadSpecial(new BigDecimal("-0.01")));
    }
}
