package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.Stock;
import com.market_view.spring.mv.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stocks", description = "Stock market data APIs")
public class StockController {

    private final StockService stockService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/symbols")
    @Operation(summary = "Get stock symbols", description = "Retrieves a list of stock symbols for a given exchange with pagination")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock symbols retrieved successfully",
                content = @Content(schema = @Schema(implementation = Stock.class))),
        @ApiResponse(responseCode = "400", description = "Invalid exchange parameter"),
        @ApiResponse(responseCode = "404", description = "Exchange not found")
    })
    public List<Stock> getStockSymbols(
            @Parameter(description = "Stock exchange code (2-4 uppercase letters)", example = "NASDAQ")
            @RequestParam @Pattern(regexp = "^[A-Z]{2,4}$", message = "Exchange must be 2-4 uppercase letters") String exchange,
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @Parameter(description = "Number of items per page (max 1000)", example = "100")
            @RequestParam(defaultValue = "100") @Max(1000) int size) {
        List<Stock> stocks = stockService.getStockSymbols(exchange);
        int fromIndex = page * size;
        int toIndex = Math.min(fromIndex + size, stocks.size());
        return stocks.subList(fromIndex, toIndex);
    }

    @GetMapping("/quote")
    @Operation(summary = "Get stock quote", description = "Retrieves real-time stock quote for a given symbol")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock quote retrieved successfully",
                content = @Content(schema = @Schema(implementation = Stock.class))),
        @ApiResponse(responseCode = "404", description = "Stock symbol not found"),
        @ApiResponse(responseCode = "400", description = "Invalid symbol parameter")
    })
    public CompletableFuture<Stock> getStockQuote(
            @Parameter(description = "Stock symbol", example = "AAPL")
            @RequestParam String symbol) {
        Stock stock = stockService.getStockQuote(symbol);
        return CompletableFuture.completedFuture(stock);
    }

    // Removed @MessageMapping("/wishlist/update/{username}") to avoid conflict
}
