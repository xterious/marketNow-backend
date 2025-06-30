package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.News;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
public class NewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    private News news1, news2, news3;

    @BeforeEach
    void setUp() {
        newsRepository.deleteAll();

        news1 = new News("business", 1710000000L, "Market Hits Record", "https://img1.jpg", "Bloomberg", "Stocks rally high", "https://news1.com");
        news2 = new News("technology", 1715000000L, "AI Disrupts Industry", "https://img2.jpg", "TechCrunch", "AI changes tech", "https://news2.com");
        news3 = new News("business", 1720000000L, "Fed Raises Rates", "https://img3.jpg", "Reuters", "Interest rate changes", "https://news3.com");

        newsRepository.saveAll(List.of(news1, news2, news3));
    }

    @Test
    void testFindByCategory() {
        List<News> businessNews = newsRepository.findByCategory("business");

        assertThat(businessNews).hasSize(2);
        assertThat(businessNews).extracting(News::getHeadline)
                .containsExactlyInAnyOrder("Market Hits Record", "Fed Raises Rates");
    }

    @Test
    void testFindByHeadline() {
        Optional<News> found = newsRepository.findByHeadline("Fed Raises Rates");

        assertThat(found).isPresent();
        assertThat(found.get().getSource()).isEqualTo("Reuters");
    }

    @Test
    void testFindRecentNews() {
        List<News> recent = newsRepository.findRecentNews();

        assertThat(recent).hasSize(3);
        assertThat(recent.get(0).getHeadline()).isEqualTo("Fed Raises Rates"); // most recent
    }

    @Test
    void testFindByAnyIdentifier() {
        Optional<News> foundByUrl = newsRepository.findByAnyIdentifier("https://news2.com");

        assertThat(foundByUrl).isPresent();
        assertThat(foundByUrl.get().getCategory()).isEqualTo("technology");

        Optional<News> foundByHeadline = newsRepository.findByAnyIdentifier("AI Disrupts Industry");

        assertThat(foundByHeadline).isPresent();
        assertThat(foundByHeadline.get().getSource()).isEqualTo("TechCrunch");
    }
}
