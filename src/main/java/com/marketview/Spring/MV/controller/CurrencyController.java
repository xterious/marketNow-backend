package com.marketview.Spring.MV.controller;



import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import com.marketview.Spring.MV.service.CurrencyService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/exchange")
    public CurrencyExchangeRate getExchangeRate(
            @RequestParam String base,
            @RequestParam String target,
            @RequestParam(defaultValue = "normal") String customerType) {
        if (!"normal".equalsIgnoreCase(customerType) && !"special".equalsIgnoreCase(customerType)) {
            throw new IllegalArgumentException("Customer type must be 'normal' or 'special'");
        }
        return currencyService.getExchangeRate(base.toUpperCase(), target.toUpperCase(), customerType.toLowerCase());
    }
}