package com.market_view.spring.mv.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.market_view.spring.mv.model.News;
import com.market_view.spring.mv.model.NewsCategory;
import com.market_view.spring.mv.repository.NewsCategoryRepository;
import com.market_view.spring.mv.repository.NewsRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceTest {

    @Mock private NewsRepository newsRepository;
    @Mock private RestTemplate restTemplate;
    @Mock private ObjectMapper objectMapper;
    @Mock private NewsCategoryRepository newsCategoryRepository;
    @Mock private WishlistService wishlistService;

    @InjectMocks private NewsService newsService;

    private String sampleApiJson;

    @BeforeEach
    void setup() {
        sampleApiJson = """
        [
            {
                "category": "general",
                "datetime": 1719800000,
                "headline": "Test Headline",
                "image": "https://img.url",
                "source": "source.com",
                "summary": "Summary here",
                "url": "https://news.url"
            }
        ]
        """;
    }

    @Test
    void testGetNewsByCategory_CacheMissFetchSuccess() throws Exception {
        ResponseEntity<String> mockResponse = new ResponseEntity<>(sampleApiJson, HttpStatus.OK);
        JsonNode node = new ObjectMapper().readTree(sampleApiJson);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);
        when(objectMapper.readTree(sampleApiJson)).thenReturn(node);

        List<News> result = newsService.getNewsByCategory("general");

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Headline", result.get(0).getHeadline());
    }

    @Test
    void testGetTopHeadlines_UsesGeneralCategory() throws Exception {
        ResponseEntity<String> mockResponse = new ResponseEntity<>(sampleApiJson, HttpStatus.OK);
        JsonNode node = new ObjectMapper().readTree(sampleApiJson);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);
        when(objectMapper.readTree(sampleApiJson)).thenReturn(node);

        List<News> result = newsService.getTopHeadlines();

        assertEquals(1, result.size());
        assertEquals("general", result.get(0).getCategory());
    }

    @Test
    void testPersistNewsToDatabase_SavesFetchedNews() throws Exception {
        ResponseEntity<String> mockResponse = new ResponseEntity<>(sampleApiJson, HttpStatus.OK);
        JsonNode node = new ObjectMapper().readTree(sampleApiJson);

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(mockResponse);
        when(objectMapper.readTree(sampleApiJson)).thenReturn(node);
        when(newsCategoryRepository.findByCategory("general")).thenReturn(Optional.empty());
        when(newsCategoryRepository.save(any())).thenReturn(new NewsCategory());

        newsService.persistNewsToDatabase();

        verify(newsRepository, times(1)).save(any(News.class));
        verify(newsCategoryRepository, times(1)).save(any(NewsCategory.class));
    }

    @Test
    void testCleanupOldNews_DeletesNotWishlisted() {
        News oldNews = new News("general", System.currentTimeMillis() - 4 * 86400000L, "Old", "", "", "", "");
        when(newsRepository.findByDatetimeLessThan(anyLong())).thenReturn(List.of(oldNews));
        when(wishlistService.filterNewsNotInWishlists(List.of(oldNews))).thenReturn(List.of(oldNews));

        newsService.cleanupOldNews();

        verify(newsRepository, times(1)).deleteAll(List.of(oldNews));
    }

    @Test
    void testClearNewsCache_LogsAndRuns() {
        // Just ensure it doesn't throw
        assertDoesNotThrow(() -> newsService.clearNewsCache());
    }
}
