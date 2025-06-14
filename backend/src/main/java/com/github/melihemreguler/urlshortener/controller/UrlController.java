package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.model.UrlRequest;
import com.github.melihemreguler.urlshortener.model.UrlResponse;
import com.github.melihemreguler.urlshortener.model.PageResponse;
import com.github.melihemreguler.urlshortener.service.UrlService;
import com.github.melihemreguler.urlshortener.dto.UrlDto;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

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
        String trimmedLongUrl = request.getTrimmedLongUrl();
        log.info("Received request to create short URL for: {}", trimmedLongUrl);

        // Calls the service layer to create and save the short URL
        String shortUrl = urlService.createAndSaveShortUrl(trimmedLongUrl);

        log.info("Returning response: long URL: {}, shortUrl: {}", trimmedLongUrl, shortUrl);
        return new UrlResponse(shortUrl);
    }

    /**
     * Lists short URLs with pagination support.
     * @param page The page number (0-based)
     * @param size The number of items per page
     * @return Paginated response containing UrlDto list
     */
    @GetMapping
    public PageResponse<UrlDto> getAllShortUrls(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return urlService.getAllShortUrls(page, size);
    }

    /**
     * Deletes a short URL by id.
     * @param id The id of the short URL to delete.
     */
    @DeleteMapping("/{id}")
    public void deleteShortUrl(@PathVariable String id) {
        urlService.deleteShortUrl(id);
    }
}
