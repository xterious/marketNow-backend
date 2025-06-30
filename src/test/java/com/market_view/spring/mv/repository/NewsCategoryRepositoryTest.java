package com.market_view.spring.mv.repository;

import com.market_view.spring.mv.model.NewsCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class NewsCategoryRepositoryTest {

    @Autowired
    private NewsCategoryRepository newsCategoryRepository;

    private NewsCategory business;
    private NewsCategory technology;

    @BeforeEach
    void setUp() {
        newsCategoryRepository.deleteAll();

        business = new NewsCategory(null, "business");
        technology = new NewsCategory(null, "technology");

        newsCategoryRepository.saveAll(List.of(business, technology));
    }

    @Test
    void testFindByCategory() {
        Optional<NewsCategory> found = newsCategoryRepository.findByCategory("business");

        assertThat(found).isPresent();
        assertThat(found.get().getCategory()).isEqualTo("business");
    }

    @Test
    void testFindAll() {
        List<NewsCategory> categories = newsCategoryRepository.findAll();

        assertThat(categories).hasSize(2);
        assertThat(categories).extracting(NewsCategory::getCategory)
                .containsExactlyInAnyOrder("business", "technology");
    }

    @Test
    void testSaveAndDelete() {
        NewsCategory finance = new NewsCategory(null, "finance");
        NewsCategory saved = newsCategoryRepository.save(finance);

        assertThat(newsCategoryRepository.findById(saved.getId())).isPresent();

        newsCategoryRepository.deleteById(saved.getId());
        assertThat(newsCategoryRepository.findById(saved.getId())).isNotPresent();
    }
}
