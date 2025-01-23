package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.model.ShortUrl;
import com.github.melihemreguler.urlshortener.service.ShortUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shorturl")
public class ShortUrlController {

    @Autowired
    private ShortUrlService shortUrlService;

    @PostMapping
    public String createShortUrl(@RequestParam String url) {
        ShortUrl shortUrl = shortUrlService.createAndSaveShortUrl(url);
        return shortUrl.getShortCode();
    }
}
