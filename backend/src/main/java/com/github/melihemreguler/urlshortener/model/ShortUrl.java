package com.github.melihemreguler.urlshortener.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "shorturls")
@Data
public class ShortUrl {

    @Indexed(unique = true)
    private String id;

    private String url;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int accessCount;

    public ShortUrl(String url, String shortCode) {
        this.url = url;
        this.shortCode = shortCode;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.accessCount = 0;
    }

    public void incrementAccessCount() {
        this.accessCount++;
    }

}
