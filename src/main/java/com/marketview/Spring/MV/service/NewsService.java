package com.marketview.Spring.MV.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.marketview.Spring.MV.model.News;
import com.marketview.Spring.MV.model.NewsCategory;
import com.marketview.Spring.MV.repository.NewsCategoryRepository;
import com.marketview.Spring.MV.repository.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class NewsService {

    private static final Logger logger = LoggerFactory.getLogger(NewsService.class);

    private final NewsRepository newsRepository;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private NewsCategoryRepository newsCategoryRepository;


    @Value("${finnhub.api.key}")
    private String finnhubApiKey;

    public NewsService(NewsRepository newsRepository, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.newsRepository = newsRepository;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "newsByCategory", key = "#category")
    public List<News> getNewsByCategory(String category) {
        List<News> cachedNews = newsRepository.findByCategory(category);
        if (!cachedNews.isEmpty()) return cachedNews;
        fetchAndStoreNews(category);
        return newsRepository.findByCategory(category);
    }

    @Cacheable(value = "topHeadlines")
    public List<News> getTopHeadlines() {
        List<News> headlines = newsRepository.findAll().stream()
                .sorted((n1, n2) -> n2.getHeadline().compareTo(n1.getHeadline())) // Proxy for recency
                .limit(5)
                .toList();
        if (!headlines.isEmpty()) return headlines;
        fetchAndStoreNews(null);
        return newsRepository.findAll().stream()
                .limit(5)
                .toList();
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    @CacheEvict(value = {"newsByCategory", "topHeadlines"}, allEntries = true)
    public void refreshNewsCache() {
        logger.info("Refreshing news cache");
        newsRepository.deleteAll();
        fetchAndStoreNews(null);
    }

    private void fetchAndStoreNews(String category) {
        String url = "https://finnhub.io/api/v1/news"
                + (category != null && !category.isEmpty() ? "?category=" + category : "")
                + "&token=" + finnhubApiKey;

        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                logger.warn("Failed to fetch news: HTTP {}", response.getStatusCode());
                return;
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

                    // Store category if new
                    newsCategoryRepository.findByCategory(cat)
                            .orElseGet(() -> newsCategoryRepository.save(new NewsCategory(null, cat)));

                    // Only save to MongoDB if you want to persist all news
                    News news = new News(
                            null,        // id (let MongoDB generate)
                            cat,
                            datetime,
                            headline,
                            image,
                            source,
                            summary,
                            urlField
                    );
                    newsRepository.save(news);
                }
            }
        } catch (Exception e) {
            logger.error("Error fetching news from Finnhub: {}", e.getMessage(), e);
        }
    }
}