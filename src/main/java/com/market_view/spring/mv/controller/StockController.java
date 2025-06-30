package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.Stock;
import com.market_view.spring.mv.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stocks", description = "Stock market data APIs")
public class StockController {

    private final StockService stockService;

    @GetMapping("/symbols")
    @Operation(
            summary = "Get stock symbols",
            description = "Retrieve paginated and searchable list of stock symbols for a given exchange"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stock symbols retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parameters")
    })
    public Page<Stock> getStockSymbols(
            @Parameter(description = "Stock exchange code (e.g. NASDAQ)")
            @RequestParam String exchange,

            @Parameter(description = "Search query for symbol or stock name", example = "AAPL")
            @RequestParam(defaultValue = "") String query,

            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "50")
            @RequestParam(defaultValue = "50") int size
    ) {
        PageRequest pageable = PageRequest.of(page, size);
        return stockService.getStockSymbols(exchange, query, pageable);
    }
}
