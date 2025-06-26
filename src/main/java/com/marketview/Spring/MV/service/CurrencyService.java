package com.marketview.Spring.MV.service;

import com.marketview.Spring.MV.model.Currency;
import com.marketview.Spring.MV.repository.CurrencyRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class CurrencyService {

    private final CurrencyRepository currencyRepository;

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }

    public Currency getCurrencyByCode(String code) {
        return currencyRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Currency not found"));
    }

    public Currency createCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public Currency updateCurrency(String code, Currency currencyDetails) {
        Currency currency = getCurrencyByCode(code);
        currency.setName(currencyDetails.getName());
        currency.setExchangeRate(currencyDetails.getExchangeRate());
        // update other fields
        return currencyRepository.save(currency);
    }

    public void deleteCurrency(String code) {
        Currency currency = getCurrencyByCode(code);
        currencyRepository.delete(currency);
    }
}
