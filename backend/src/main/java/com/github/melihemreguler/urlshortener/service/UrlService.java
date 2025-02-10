package com.github.melihemreguler.urlshortener.service;

import com.github.melihemreguler.urlshortener.config.AppConfig;
import com.github.melihemreguler.urlshortener.dto.UrlDto;
import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import com.github.melihemreguler.urlshortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        // Check if a short URL already exists for the given path
        Optional<UrlDto> existingUrl = urlRepository.findByLongUrl(longUrl);
        if (existingUrl.isPresent()) {
            String shortCode = existingUrl.get().getShortCode();
            String shortUrl = createShortUrl(shortCode);
            log.debug("Existing short code found for: {}, returning existing shortUrl: {}", longUrl, shortUrl);
            return shortUrl;
        }

        // Generate a new short URL
        String randomCode = generateRandomCode();

        UrlDto urlDto = new UrlDto(longUrl, randomCode);
        urlRepository.save(urlDto);
        log.debug("Generated new shortCode: {} for URL: {}", randomCode, longUrl);
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
}
