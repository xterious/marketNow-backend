package com.marketview.Spring.MV.controller;

import com.marketview.Spring.MV.model.News;
import com.marketview.Spring.MV.service.NewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/news")
public class NewsController {

    @Autowired
    private NewsService newsService;

    @GetMapping
    public List<News> getAllNews() {
        return newsService.getAllNews();
    }

    @GetMapping("/{id}")
    public News getNewsById(@PathVariable String id) {
        return newsService.getNewsById(id);
    }

    @PostMapping
    public News createNews(@RequestBody News news) {
        return newsService.createNews(news);
    }

    @PutMapping("/{id}")
    public News updateNews(@PathVariable String id, @RequestBody News news) {
        return newsService.updateNews(id, news);
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable String id) {
        newsService.deleteNews(id);
    }
}
