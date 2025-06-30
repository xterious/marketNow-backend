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
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import java.io.InputStream;
import java.util.ArrayList;

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
    @Autowired
    private ObjectMapper objectMapper;

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

    @PostMapping("")
    @Operation(summary = "Add a new stock", description = "Parses and saves a single stock to MongoDB")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock saved successfully",
                content = @Content(schema = @Schema(implementation = Stock.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    public Stock addStock(@RequestBody StockInputDto stockInput) {
        // Map input to Stock model as per new requirements
        Stock stock = new Stock(
            stockInput.getDisplaySymbol(), // stockSymbol
            stockInput.getCurrency(),      // marketOrigin
            stockInput.getDescription(),   // stockName
            stockInput.getType()           // type
        );
        return stockService.saveStock(stock);
    }

    @PostMapping("/import-stocks")
    @Operation(summary = "Import stocks from symbol-list.json", description = "Reads symbol-list.json and saves all stocks to the database.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stocks imported successfully"),
        @ApiResponse(responseCode = "500", description = "Error importing stocks")
    })
    public ResponseEntity<String> importStocks() {
        try {
            // Load symbol-list.json from the root of the classpath
            ClassPathResource resource = new ClassPathResource("symbol-list.json");
            InputStream inputStream = resource.getInputStream();
            JsonNode root = objectMapper.readTree(inputStream);
            int imported = 0;
            if (root.isArray()) {
                for (JsonNode node : root) {
                    String stockSymbol = node.path("displaySymbol").asText();
                    String marketOrigin = node.path("currency").asText();
                    String stockName = node.path("description").asText();
                    String type = node.path("type").asText();
                    Stock stock = new Stock(stockSymbol, marketOrigin, stockName, type);
                    stockService.saveStock(stock);
                    imported++;
                }
            }
            return ResponseEntity.ok("Imported " + imported + " stocks.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error importing stocks: " + e.getMessage());
        }
    }

    // Removed @MessageMapping("/wishlist/update/{username}") to avoid conflict

    // DTO for parsing incoming stock JSON
    public static class StockInputDto {
        private String currency;
        private String description;
        private String displaySymbol;
        private String figi;
        private String isin;
        private String mic;
        private String shareClassFIGI;
        private String symbol;
        private String symbol2;
        private String type;

        // Getters and setters
        public String getCurrency() { return currency; }
        public void setCurrency(String currency) { this.currency = currency; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getDisplaySymbol() { return displaySymbol; }
        public void setDisplaySymbol(String displaySymbol) { this.displaySymbol = displaySymbol; }
        public String getFigi() { return figi; }
        public void setFigi(String figi) { this.figi = figi; }
        public String getIsin() { return isin; }
        public void setIsin(String isin) { this.isin = isin; }
        public String getMic() { return mic; }
        public void setMic(String mic) { this.mic = mic; }
        public String getShareClassFIGI() { return shareClassFIGI; }
        public void setShareClassFIGI(String shareClassFIGI) { this.shareClassFIGI = shareClassFIGI; }
        public String getSymbol() { return symbol; }
        public void setSymbol(String symbol) { this.symbol = symbol; }
        public String getSymbol2() { return symbol2; }
        public void setSymbol2(String symbol2) { this.symbol2 = symbol2; }
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }
}
