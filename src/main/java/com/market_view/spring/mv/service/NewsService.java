package com.market_view.spring.mv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market_view.spring.mv.model.News;
import com.market_view.spring.mv.model.NewsCategory;
import com.market_view.spring.mv.repository.NewsCategoryRepository;
import com.market_view.spring.mv.repository.NewsRepository;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.ArrayList;

@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final NewsCategoryRepository newsCategoryRepository;
    private final WishlistService wishlistService;

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    public NewsService(NewsRepository newsRepository, RestTemplate restTemplate,
                       ObjectMapper objectMapper, NewsCategoryRepository newsCategoryRepository,
                       WishlistService wishlistService) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.newsCategoryRepository = newsCategoryRepository;
        this.wishlistService = wishlistService;
    }

    @PostConstruct
    public void init() {
        logger.info("Finnhub API Key loaded: {}", finnhubApiKey != null ? "" : "NULL");
    }

    // Cache-first approach: Check cache -> If not found, fetch from API -> Cache the result
    @Cacheable(value = "newsByCategory", key = "#category")
    public List<News> getNewsByCategory(String category) {
        logger.info("Cache miss for category: {}. Fetching from API.", category);
        return fetchNewsFromAPI(category);
    }

    @Cacheable(value = "topHeadlines")
    public List<News> getTopHeadlines() {
        logger.info("Cache miss for top headlines. Fetching from API.");
        return fetchNewsFromAPI("general");
    }

    // Private method to fetch news from external API
    private List<News> fetchNewsFromAPI(String category) {
        String url = "https://finnhub.io/api/v1/news?" +
                (category != null && !category.isEmpty() ? "category=" + category : "category=general") +
                "&token=" + finnhubApiKey;

        logger.info("Fetching news from URL: {}", url);
        List<News> newsList = new ArrayList<>();

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.warn("Failed to fetch news: HTTP {}", response.getStatusCode());
                return newsList;
            }

            JsonNode jsonArray = objectMapper.readTree(response.getBody());
            if (jsonArray.isArray()) {
                for (JsonNode node : jsonArray) {
                    String cat = node.has("category") ? node.get("category").asText() : "general";
                    String headline = node.has("headline") ? node.get("headline").asText() : "No headline";
                    String summary = node.has("summary") ? node.get("summary").asText() : "";
                    String urlField = node.has("url") ? node.get("url").asText() : "";
                    String source = node.has("source") ? node.get("source").asText() : "";
                    String image = node.has("image") ? node.get("image").asText() : "";
                    long datetime = node.has("datetime") ? node.get("datetime").asLong() : System.currentTimeMillis();

                    News news = new News(cat, datetime, headline, image, source, summary, urlField);
                    newsList.add(news);
                }
            }
            logger.info("Fetched {} news articles from API", newsList.size());
        } catch (Exception e) {
            logger.error("Error fetching news from Finnhub: {}", e.getMessage(), e);
        }

        return newsList;
    }

    // Scheduled task: Persist news to database every 2-3 hours
    @Scheduled(fixedRate = 7200000) // 2 hours in milliseconds
    @Transactional
    public void persistNewsToDatabase() {
        logger.info("Starting scheduled news persistence to database");

        try {
            List<News> freshNews = fetchNewsFromAPI("general");
            for (News news : freshNews) {
                // Store category if new
                newsCategoryRepository.findByCategory(news.getCategory())
                        .orElseGet(() -> newsCategoryRepository.save(new NewsCategory(null, news.getCategory())));

                // Save news to database
                newsRepository.save(news);
            }
            logger.info("Successfully persisted {} news articles to database", freshNews.size());
        } catch (Exception e) {
            logger.error("Error during scheduled news persistence: {}", e.getMessage(), e);
        }
    }

    // Scheduled task: Clean up old news daily (delegate wishlist checking to WishlistService)
    @Scheduled(cron = "0 0 2 * * ?") // Daily at 2 AM
    @Transactional
    public void cleanupOldNews() {
        logger.info("Starting cleanup of old news articles");

        try {
            long threeDaysAgo = System.currentTimeMillis() - (3 * 24 * 60 * 60 * 1000L);

            // Get all news articles older than 3 days
            List<News> oldNews = newsRepository.findByDatetimeLessThan(threeDaysAgo);

            if (oldNews.isEmpty()) {
                logger.info("No old news articles found to process");
                return;
            }

            // Use WishlistService to filter out wishlisted news
            List<News> newsToDelete = wishlistService.filterNewsNotInWishlists(oldNews);

            if (!newsToDelete.isEmpty()) {
                newsRepository.deleteAll(newsToDelete);
                logger.info("Deleted {} old news articles from database (preserved {} wishlisted articles)",
                        newsToDelete.size(), oldNews.size() - newsToDelete.size());
            } else {
                logger.info("All {} old news articles are in wishlists - no deletion performed", oldNews.size());
            }

        } catch (Exception e) {
            logger.error("Error during news cleanup: {}", e.getMessage(), e);
        }
    }

    // Manual cache eviction method
    @CacheEvict(value = {"newsByCategory", "topHeadlines"}, allEntries = true)
    public void clearNewsCache() {
        logger.info("Manually cleared news cache");
    }
}