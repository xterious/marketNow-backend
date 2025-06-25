package com.marketview.Spring.MV.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "news")
public class News {
    @Id
    private String id;
    private String category;
    private String headline;
    private String summary;
    private String url;

    public News() {}

    public News(String category, String headline, String summary, String url) {
        this.category = category;
        this.headline = headline;
        this.summary = summary;
        this.url = url;
    }

    // Getters and setters
    public String getId() { return id; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getHeadline() { return headline; }
    public void setHeadline(String headline) { this.headline = headline; }
    public String getSummary() { return summary; }
    public void setSummary(String summary) { this.summary = summary; }
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
}