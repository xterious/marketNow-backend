package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.Currency;
import com.marketview.Spring.MV.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    @Autowired
    private CurrencyService currencyService;

    @GetMapping
    public List<Currency> getAllCurrencies() {
        return currencyService.getAllCurrencies();
    }

    @GetMapping("/{code}")
    public Currency getCurrencyByCode(@PathVariable String code) {
        return currencyService.getCurrencyByCode(code);
    }

    @PostMapping
    public Currency createCurrency(@RequestBody Currency currency) {
        return currencyService.createCurrency(currency);
    }

    @PutMapping("/{code}")
    public Currency updateCurrency(@PathVariable String code, @RequestBody Currency currency) {
        return currencyService.updateCurrency(code, currency);
    }

    @DeleteMapping("/{code}")
    public void deleteCurrency(@PathVariable String code) {
        currencyService.deleteCurrency(code);
    }
}
