package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.News;
import com.marketview.Spring.MV.repository.NewsRepository;
import com.marketview.Spring.MV.service.NewsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public class NewsWebSocketController {

    private static final Logger logger = LoggerFactory.getLogger(NewsWebSocketController.class);
    private final NewsService newsService;
    private final NewsRepository newsRepository;

    public NewsWebSocketController(NewsService newsService, NewsRepository newsRepository) {
        this.newsService = newsService;
        this.newsRepository = newsRepository;
    }

    @MessageMapping("/news")
    @SendTo("/topic/news")
    public List<News> getTopHeadlines(@AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching top headlines for user: {}", userDetails.getUsername());
        return newsService.getTopHeadlines();
    }

    @MessageMapping("/news/category")
    @SendTo("/topic/news/category")
    public List<News> getNewsByCategory(NewsCategoryRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching news for category: {} by user: {}", request.getCategory(), userDetails.getUsername());
        return newsService.getNewsByCategory(request.getCategory());
    }

    @MessageMapping("/news/details")
    @SendTo("/topic/news/details")
    public News getNewsDetails(NewsDetailsRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        logger.info("Fetching news details for identifier: {} by user: {}", request.getIdentifier(), userDetails.getUsername());
        Optional<News> news = newsRepository.findByAnyIdentifier(request.getIdentifier());
        return news.orElseThrow(() -> new IllegalArgumentException("News not found for identifier: " + request.getIdentifier()));
    }

    public static class NewsCategoryRequest {
        private String category;
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }
    }

    public static class NewsDetailsRequest {
        private String identifier;
        public String getIdentifier() { return identifier; }
        public void setIdentifier(String identifier) { this.identifier = identifier; }
    }
}