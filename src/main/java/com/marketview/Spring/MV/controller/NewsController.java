package com.marketview.Spring.MV.controller;
import com.marketview.Spring.MV.model.News;
import com.marketview.Spring.MV.service.NewsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {
    private final NewsService newsService;

    @GetMapping("/getAll")
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/certain-category")
    public List<News> getNewsByCategory(@RequestParam String category) {
        return newsService.getNewsByCategory(category);
    }

    @GetMapping("/stock-wise-news")
    public List<News> getNewsByStock(@RequestParam String symbol) {
        return newsService.getNewsByStock(symbol);
    }
}
