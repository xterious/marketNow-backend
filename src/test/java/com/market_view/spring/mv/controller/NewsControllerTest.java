package com.market_view.spring.mv.controller;

import com.market_view.spring.mv.config.TestSecurityConfig;
import com.market_view.spring.mv.model.News;
import com.market_view.spring.mv.model.NewsCategory;
import com.market_view.spring.mv.repository.NewsCategoryRepository;
import com.market_view.spring.mv.service.NewsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {NewsController.class, TestSecurityConfig.class})
@AutoConfigureMockMvc

public class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NewsService newsService;

    @MockBean
    private NewsCategoryRepository newsCategoryRepository;

    private News news1;
    private News news2;

    @BeforeEach
    public void setup() {
        news1 = new News("business", 1715000000L, "Market Rally", "https://image.com/1.jpg", "CNBC", "Stocks up", "https://news1.com");
        news2 = new News("technology", 1716000000L, "AI Boom", "https://image.com/2.jpg", "TechCrunch", "AI transforming industry", "https://news2.com");
    }

    @WithMockUser
    @Test
    public void testGetNewsByCategory() throws Exception {
        when(newsService.getNewsByCategory("technology")).thenReturn(List.of(news2));

        mockMvc.perform(get("/api/news/category")
                        .param("category", "technology")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].headline").value("AI Boom"));
    }

    @WithMockUser
    @Test
    public void testGetTopHeadlines() throws Exception {
        when(newsService.getTopHeadlines()).thenReturn(Arrays.asList(news1, news2));

        mockMvc.perform(get("/api/news/headlines")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].headline").value("Market Rally"))
                .andExpect(jsonPath("$[1].headline").value("AI Boom"));
    }

    @WithMockUser
    @Test
    public void testGetAllCategories() throws Exception {
        List<NewsCategory> categories = List.of(
                new NewsCategory("1", "business"),
                new NewsCategory("2", "technology")
        );

        when(newsCategoryRepository.findAll()).thenReturn(categories);

        mockMvc.perform(get("/api/news/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("business"))
                .andExpect(jsonPath("$[1]").value("technology"));
    }
}
