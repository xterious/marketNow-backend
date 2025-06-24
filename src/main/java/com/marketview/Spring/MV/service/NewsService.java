package com.marketview.Spring.MV.service;
import com.marketview.Spring.MV.model.News;
import com.marketview.Spring.MV.repository.NewsRepository;
import com.marketview.Spring.MV.util.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NewsService {
    private final NewsRepository newsRepository;

    @Value("${finnhub.api.key}")
    private String finnhubApiKey;



    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public List<News> getNewsByCategory(String category) {
        return newsRepository.findByCategory(category);
    }

    public List<News> getNewsByStock(String symbol) {
        try {
            String url = String.format(
                    "https://finnhub.io/api/v1/company-news?symbol=%s&from=%s&to=%s&token=%s",
                    symbol,
                    LocalDateTime.now().minusDays(7).toLocalDate(),
                    LocalDateTime.now().toLocalDate(),
                    finnhubApiKey
            );
            RestTemplate restTemplate = new RestTemplate();
            News[] response = restTemplate.getForObject(url, News[].class);
            // Optionally save to DB
            if (response != null && response.length > 0) {
                newsRepository.saveAll(Arrays.asList(response));
                return Arrays.asList(response);
            }
            return List.of();
        } catch (Exception e) {
            throw new CustomException(e.getMessage(), HttpStatus.NO_CONTENT);
        }
    }
}
