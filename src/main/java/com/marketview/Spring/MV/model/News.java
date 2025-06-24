package com.marketview.Spring.MV.model;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "news")
public class News {
    @Id
    private String id;
    private String headline;
    private String summary;
    private String category;
    private String source;
    private String url;
    private String relatedStock;
    private LocalDateTime datetime;
}
