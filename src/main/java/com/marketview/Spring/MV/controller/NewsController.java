package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.News;
import com.marketview.Spring.MV.model.NewsCategory;
import com.marketview.Spring.MV.repository.NewsCategoryRepository;
import com.marketview.Spring.MV.service.NewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
@Tag(name = "News", description = "Financial news and headlines APIs")
public class NewsController {

    private final NewsService newsService;
    private final NewsCategoryRepository newsCategoryRepository;

    public NewsController(NewsService newsService, NewsCategoryRepository newsCategoryRepository) {
        this.newsService = newsService;
        this.newsCategoryRepository = newsCategoryRepository;
    }

    @GetMapping("/category")
    @Operation(summary = "Get news by category", description = "Retrieves news articles filtered by a specific category")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "News articles retrieved successfully",
                content = @Content(schema = @Schema(implementation = News.class))),
        @ApiResponse(responseCode = "400", description = "Invalid category parameter"),
        @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public List<News> getNewsByCategory(
            @Parameter(description = "News category", example = "technology")
            @RequestParam String category) {
        return newsService.getNewsByCategory(category);
    }

    @GetMapping("/headlines")
    @Operation(summary = "Get top headlines", description = "Retrieves the latest top headlines from various sources")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Top headlines retrieved successfully",
                content = @Content(schema = @Schema(implementation = News.class)))
    })
    public List<News> getTopHeadlines() {
        return newsService.getTopHeadlines();
    }

    @GetMapping("/categories")
    @Operation(summary = "Get all news categories", description = "Retrieves a list of all available news categories")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Categories retrieved successfully",
                content = @Content(schema = @Schema(implementation = String.class)))
    })
    public List<String> getAllCategories() {
        // Fetch all categories from the news_categories collection
        return newsCategoryRepository.findAll()
                .stream()
                .map(NewsCategory::getCategory)
                .distinct()
                .toList();
    }
}