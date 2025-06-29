package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import com.marketview.Spring.MV.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyController.class);

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/exchange")
    public ResponseEntity<CurrencyExchangeRate> getExchangeRate(
            @RequestParam String base,
            @RequestParam String target,
            @RequestParam(defaultValue = "normal") String customerType) {
        String normalizedBase = base.toUpperCase();
        String normalizedTarget = target.toUpperCase();
        String normalizedCustomerType = customerType.toLowerCase();
        String id = String.format("%s-%s-%s", normalizedBase, normalizedTarget, normalizedCustomerType);

        try {
            if (!"normal".equalsIgnoreCase(normalizedCustomerType) && !"special".equalsIgnoreCase(normalizedCustomerType)) {
                logger.warn("Invalid customer type: {} for ID: {}", normalizedCustomerType, id);
                return ResponseEntity.badRequest().body(null);
            }

            if (!currencyService.isValidCurrencyPair(normalizedBase, normalizedTarget)) {
                logger.warn("Invalid currency pair: {}/{} for ID: {}", normalizedBase, normalizedTarget, id);
                return ResponseEntity.notFound().build();
            }

            CurrencyExchangeRate rate = currencyService.getExchangeRate(normalizedBase, normalizedTarget, normalizedCustomerType);
            return ResponseEntity.ok(rate);
        } catch (Exception e) {
            logger.error("Error processing exchange rate request for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }

    @GetMapping("/convert")
    public ResponseEntity<BigDecimal> convertCurrency(
            @RequestParam String base,
            @RequestParam String target,
            @RequestParam BigDecimal amount,
            @RequestParam(defaultValue = "normal") String customerType) {
        String normalizedBase = base.toUpperCase();
        String normalizedTarget = target.toUpperCase();
        String normalizedCustomerType = customerType.toLowerCase();
        String id = String.format("%s-%s-%s", normalizedBase, normalizedTarget, normalizedCustomerType);

        try {
            if (!"normal".equalsIgnoreCase(normalizedCustomerType) && !"special".equalsIgnoreCase(normalizedCustomerType)) {
                logger.warn("Invalid customer type: {} for ID: {}", normalizedCustomerType, id);
                return ResponseEntity.badRequest().body(null);
            }

            if (!currencyService.isValidCurrencyPair(normalizedBase, normalizedTarget)) {
                logger.warn("Invalid currency pair: {}/{} for ID: {}", normalizedBase, normalizedTarget, id);
                return ResponseEntity.notFound().build();
            }

            BigDecimal convertedAmount = currencyService.convertCurrency(normalizedBase, normalizedTarget, amount, normalizedCustomerType);
            return ResponseEntity.ok(convertedAmount);
        } catch (Exception e) {
            logger.error("Error processing currency conversion request for ID {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(null);
        }
    }
}