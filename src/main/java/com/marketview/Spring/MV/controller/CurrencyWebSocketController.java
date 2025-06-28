package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.CurrencyExchangeRate;
import com.marketview.Spring.MV.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class CurrencyWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(CurrencyWebSocketController.class);
    private final CurrencyService currencyService;

    public CurrencyWebSocketController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @MessageMapping("/currency/exchange")
    @SendTo("/topic/currency/exchange")
    public CurrencyExchangeRate getExchangeRate(CurrencyRequest request) {
        logger.info("Fetching exchange rate: base={}, target={}, customerType={}",
                request.getBase(), request.getTarget(), request.getCustomerType());
        String customerType = request.getCustomerType() != null ? request.getCustomerType().toLowerCase() : "normal";
        if (!"normal".equalsIgnoreCase(customerType) && !"special".equalsIgnoreCase(customerType)) {
            throw new IllegalArgumentException("Customer type must be 'normal' or 'special'");
        }
        return currencyService.getExchangeRate(
                request.getBase().toUpperCase(), request.getTarget().toUpperCase(), customerType);
    }

    public static class CurrencyRequest {
        private String base;
        private String target;
        private String customerType;
        public String getBase() { return base; }
        public void setBase(String base) { this.base = base; }
        public String getTarget() { return target; }
        public void setTarget(String target) { this.target = target; }
        public String getCustomerType() { return customerType; }
        public void setCustomerType(String customerType) { this.customerType = customerType; }
    }
}