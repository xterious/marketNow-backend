package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.model.News;
import com.market_view.spring.mv.model.NewsCategory;
import com.market_view.spring.mv.repository.NewsCategoryRepository;
import com.market_view.spring.mv.service.NewsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/news")
public class NewsController {

    private final NewsService newsService;
    private final NewsCategoryRepository newsCategoryRepository;

    public NewsController(NewsService newsService, NewsCategoryRepository newsCategoryRepository) {
        this.newsService = newsService;
        this.newsCategoryRepository = newsCategoryRepository;
    }

    @GetMapping("/category")
    public List<News> getNewsByCategory(@RequestParam String category) {
        return newsService.getNewsByCategory(category);
    }

    @GetMapping("/headlines")
    public List<News> getTopHeadlines() {
        return newsService.getTopHeadlines();
    }
    @GetMapping("/categories")
    public List<String> getAllCategories() {
        // Fetch all categories from the news_categories collection
        return newsCategoryRepository.findAll()
                .stream()
                .map(NewsCategory::getCategory)
                .distinct()
                .toList();
    }
}