package com.github.melihemreguler.urlshortener.service;

import com.github.melihemreguler.urlshortener.model.ShortUrl;
import com.github.melihemreguler.urlshortener.repository.ShortUrlRepository;
import com.mongodb.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ShortUrlService {

    @Autowired
    private ShortUrlRepository shortUrlRepository;

    public ShortUrl createAndSaveShortUrl(String url) {
        try {
            Optional<ShortUrl> existingShortUrl = shortUrlRepository.findByUrl(url);
            if (existingShortUrl.isPresent()) {
                return existingShortUrl.get();
            }

            String randomCode = generateRandomCode();
            ShortUrl shortUrl = new ShortUrl(url, randomCode);
            return shortUrlRepository.save(shortUrl);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("A short URL for this URL already exists.");
        }
    }

    private String generateRandomCode() {
        return UUID.randomUUID().toString().substring(0, 8);
    }
}
