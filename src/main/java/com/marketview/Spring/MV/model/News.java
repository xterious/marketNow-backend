package com.marketview.Spring.MV.model;

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
    private String category;      // e.g., "company news"
    private Long datetime;        // Unix timestamp or ISO date
    private String headline;      // News headline/title
    private String newsId;        // External API's unique ID (e.g., "25287")
    private String image;         // URL to news image
    private String related;       // Related stock symbol(s), e.g., "AAPL"
    private String source;        // News source, e.g., "Business Insider"
    private String summary;       // Short summary/description
    private String url;           // Link to full article

    public News(String cat, long datetime, String headline, String image, String source, String summary, String urlField) {
        this.category = cat;
        this.datetime = datetime;
        this.headline = headline;
        this.image = image;
        this.source = source;
        this.summary = summary;
        this.url = urlField;
    }
}
