package com.marketview.Spring.MV.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FixerSymbolsResponseDto {
    private boolean success;
    private Map<String, String> symbols;
}
