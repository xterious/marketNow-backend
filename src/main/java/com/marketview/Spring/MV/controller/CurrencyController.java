package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import com.marketview.Spring.MV.repository.CurrencyRepository;
import com.marketview.Spring.MV.service.CurrencyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
public class CurrencyController {

    private final CurrencyService currencyService;


    @GetMapping("/symbols")
    public ResponseEntity<Map<String, String>> getSymbols() {
        Map<String, String> symbols = currencyService.getAvailableSymbols();
        return ResponseEntity.ok(symbols);
    }


    @GetMapping("/exchange")
    public ResponseEntity<CurrencyExchangeRate> getExchangeRate(
            @RequestParam String base,
            @RequestParam String target,
            @RequestParam(defaultValue = "normal") String customerType
    ) {
        String normalizedBase = base.toUpperCase();
        String normalizedTarget = target.toUpperCase();
        String normalizedCustomerType = customerType.toLowerCase();

        if (!"normal".equals(normalizedCustomerType) && !"special".equals(normalizedCustomerType)) {
            return ResponseEntity.badRequest().body(null);
        }

        CurrencyExchangeRate rate = currencyService.getExchangeRate(normalizedBase, normalizedTarget, normalizedCustomerType);
        return ResponseEntity.ok(rate);
    }

    @GetMapping("/convert")
    public ResponseEntity<BigDecimal> convertCurrency(
            @RequestParam String base,
            @RequestParam String target,
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "normal") String customerType
    ) {
        String normalizedBase = base.toUpperCase();
        String normalizedTarget = target.toUpperCase();
        String normalizedCustomerType = customerType.toLowerCase();

        if (!"normal".equals(normalizedCustomerType) && !"special".equals(normalizedCustomerType)) {
            return ResponseEntity.badRequest().body(null);
        }

        BigDecimal convertedAmount = currencyService.convertCurrency(normalizedBase, normalizedTarget, amount, normalizedCustomerType);
        return ResponseEntity.ok(convertedAmount);
    }
}
