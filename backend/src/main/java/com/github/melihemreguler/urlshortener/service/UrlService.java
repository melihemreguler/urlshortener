package com.github.melihemreguler.urlshortener.service;

import com.github.melihemreguler.urlshortener.config.AppConfig;
import com.github.melihemreguler.urlshortener.dto.UrlDto;
import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import com.github.melihemreguler.urlshortener.model.PageResponse;
import com.github.melihemreguler.urlshortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    private final AppConfig appConfig;

    // Constructor for dependency injection of UrlRepository
    @Autowired
    public UrlService(UrlRepository UrlRepository, AppConfig appConfig) {
        this.urlRepository = UrlRepository;
        this.appConfig = appConfig;
    }

    /**
     * Generates and saves a short URL for the given long URL.
     * If a short URL already exists, it returns the existing one.
     *
     * @param longUrl The long URL to be shortened.
     * @return The generated or existing short URL.
     */
    public String createAndSaveShortUrl(String longUrl) {
        // Final safety net: trim the URL at service level
        String trimmedLongUrl = longUrl != null ? longUrl.trim() : "";
        
        if (trimmedLongUrl.isEmpty()) {
            throw new IllegalArgumentException("Long URL cannot be empty after trimming");
        }

        // Check if a short URL already exists for the given path
        Optional<UrlDto> existingUrl = urlRepository.findByLongUrl(trimmedLongUrl);
        if (existingUrl.isPresent()) {
            String shortCode = existingUrl.get().getShortCode();
            String shortUrl = createShortUrl(shortCode);
            log.debug("Existing short code found for: {}, returning existing shortUrl: {}", trimmedLongUrl, shortUrl);
            return shortUrl;
        }

        // Generate a new short URL
        String randomCode = generateRandomCode();

        UrlDto urlDto = new UrlDto(trimmedLongUrl, randomCode);
        urlRepository.save(urlDto);
        log.debug("Generated new shortCode: {} for URL: {}", randomCode, trimmedLongUrl);
        return createShortUrl(urlDto.getShortCode());

    }

    /**
     * Retrieves the long URL associated with a given short code.
     *
     * @param shortUrl The short code to look up.
     * @return The long URL associated with the short code.
     * @throws UrlNotFoundException if the short code does not exist.
     */
    public String getLongUrl(String shortUrl) {

        Optional<UrlDto> existingUrl = urlRepository.findByShortCode(shortUrl);
        if (existingUrl.isEmpty()) {
            throw new UrlNotFoundException("URL not found", shortUrl);
        }
        UrlDto urlDto = existingUrl.get();
        urlDto.incrementAccessCount(); // Update the access count
        urlRepository.save(urlDto);
        log.debug("long url found for: {}, long url: {}", shortUrl, urlDto.getLongUrl());
        return urlDto.getLongUrl();
    }

    /**
     * Generates a random short code for a URL.
     *
     * @return A randomly generated short code.
     */
    private String generateRandomCode() {
        String randomCode = UUID.randomUUID().toString().substring(0, 8);
        log.debug("Generated random shortCode: {}", randomCode);
        return randomCode;
    }

    /**
     * Creates a short URL from a given short code with the service URL.
     *
     * @param shortCode The short code to be converted to a short URL.
     * @return The short URL.
     */
    private String createShortUrl(String shortCode) {
        return appConfig.getServiceUrl() + "/" + shortCode;
    }

    /**
     * Returns all short URLs.
     * @return List of UrlDto
     */
    public List<UrlDto> getAllShortUrls() {
        return urlRepository.findAll();
    }

    /**
     * Returns paginated short URLs.
     * @param page The page number (0-based)
     * @param size The number of items per page
     * @return PageResponse containing UrlDto list
     */
    public PageResponse<UrlDto> getAllShortUrls(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<UrlDto> urlPage = urlRepository.findAll(pageable);
        
        return new PageResponse<>(
            urlPage.getContent(),
            urlPage.getNumber(),
            urlPage.getSize(),
            urlPage.getTotalElements(),
            urlPage.getTotalPages(),
            urlPage.isFirst(),
            urlPage.isLast()
        );
    }

    /**
     * Deletes a short URL by id.
     * @param id The id of the short URL to delete.
     */
    public void deleteShortUrl(String id) {
        urlRepository.deleteById(id);
    }
}
