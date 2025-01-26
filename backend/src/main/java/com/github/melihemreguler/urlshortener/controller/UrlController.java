package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.service.UrlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/url")
@Slf4j
public class UrlController {

    private final UrlService urlService;


    // Constructor for dependency injection of UrlService
    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }


    /**
     * Creates a short URL for the provided long URL.
     *
     * @param longUrl The long URL that needs to be shortened.
     * @return The generated short URL.
     */
    @PostMapping
    public String createShortUrl(@RequestParam String longUrl) {
        log.info("Received request to create short URL for: {}", longUrl);

        // Calls the service layer to create and save the short URL
        String shortUrl  = urlService.createAndSaveShortUrl(longUrl);

        log.info("Returning response to create short url request for long url: {}, short url: {}", longUrl, shortUrl);
        return shortUrl;
    }
}
