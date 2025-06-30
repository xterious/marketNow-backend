package com.marketview.Spring.MV.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixerLatestRatesResponseDto {
    private boolean success;
    private long timestamp;
    private String base;
    private String date;
    private Map<String, BigDecimal> rates;
}
