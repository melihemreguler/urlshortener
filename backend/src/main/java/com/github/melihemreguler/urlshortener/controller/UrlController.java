package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.model.UrlRequest;
import com.github.melihemreguler.urlshortener.model.UrlResponse;
import com.github.melihemreguler.urlshortener.service.UrlService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/url")
@Slf4j
public class UrlController {

    private final UrlService urlService;

    @Autowired
    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    /**
     * Creates a short URL for the provided long URL.
     *
     * @param request The request body containing the long URL.
     * @return A JSON response containing the short URL.
     */
    @PostMapping
    public UrlResponse createShortUrl(@RequestBody @Valid UrlRequest request) {
        log.info("Received request to create short URL for: {}", request.longUrl());

        // Calls the service layer to create and save the short URL
        String shortUrl = urlService.createAndSaveShortUrl(request.longUrl());

        log.info("Returning response: long URL: {}, shortUrl: {}", request.longUrl(), shortUrl);
        return new UrlResponse(shortUrl);
    }
}
