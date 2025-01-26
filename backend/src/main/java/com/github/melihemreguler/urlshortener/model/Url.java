package com.github.melihemreguler.urlshortener.model;

import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "urls")
@Data
public class Url {

    @Indexed(unique = true)
    private String id;

    private String path;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int accessCount;

    public Url(String path, String shortCode) {
        this.path = path;
        this.shortCode = shortCode;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.accessCount = 0;
    }

    public void incrementAccessCount() {
        this.accessCount++;
    }

}
