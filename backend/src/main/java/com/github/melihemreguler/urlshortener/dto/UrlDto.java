package com.github.melihemreguler.urlshortener.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "urls")
@Data
public class UrlDto {

    @Indexed(unique = true)
    @Id
    private String id;

    private String longUrl;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private int accessCount;

    public UrlDto(String longUrl, String shortCode) {
        this.longUrl = longUrl;
        this.shortCode = shortCode;
        this.createdAt = LocalDateTime.now();
        this.accessCount = 0;
    }

    public void incrementAccessCount() {
        this.accessCount++;
    }

}
