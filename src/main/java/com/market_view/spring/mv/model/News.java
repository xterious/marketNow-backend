package com.market_view.spring.mv.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "news")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class News {
    @Id
    private String id;
    private String category;      // e.g., "company news"
    private Long datetime;        // Unix timestamp or ISO date
    private String headline;      // News headline/title
    private String newsId;        // External API's unique ID (e.g., "25287")
    private String image;         // URL to news image
    private String related;       // Related stock symbol(s), e.g., "AAPL"
    private String source;        // News source, e.g., "Business Insider"
    private String summary;       // Short summary/description
    private String url;           // Link to full article

    public News(String category, long datetime, String headline, String image, String source, String summary, String url) {
        this.category = category;
        this.datetime = datetime;
        this.headline = headline;
        this.image = image;
        this.source = source;
        this.summary = summary;
        this.url = url;
    }
}
