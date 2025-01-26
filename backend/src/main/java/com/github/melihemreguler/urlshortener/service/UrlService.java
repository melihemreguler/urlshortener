package com.github.melihemreguler.urlshortener.service;

import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import com.github.melihemreguler.urlshortener.model.Url;
import com.github.melihemreguler.urlshortener.repository.UrlRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
public class UrlService {

    private final UrlRepository urlRepository;
    @Value("${SHORT_URL_DOMAIN}")
    private String shortUrlDomain;
    @Value("${LONG_URL_DOMAIN}")
    private String longUrlDomain;
    @Value("${PROTOCOL}")
    private String protocol;

    // Constructor for dependency injection of UrlRepository
    @Autowired
    public UrlService(UrlRepository UrlRepository) {
        this.urlRepository = UrlRepository;
    }

    /**
     * Generates and saves a short URL for the given long URL.
     * If a short URL already exists, it returns the existing one.
     *
     * @param longUrl The long URL to be shortened.
     * @return The generated or existing short URL.
     */
    public String createAndSaveShortUrl(String longUrl) {
        String path = UrlPathExtractor.extractPath(longUrl);

        // Check if a short URL already exists for the given path
        Optional<Url> existingUrl = urlRepository.findByPath(path);
        if (existingUrl.isPresent()) {
            String shortCode = existingUrl.get().getShortCode();
            String shortUrl = createUrl(shortUrlDomain, shortCode);
            log.info("Existing short URL found for: {}, returning existing shortUrl: {}", longUrl, shortUrl);

            return shortUrl;
        }

        // Generate a new short URL
        String randomCode = generateRandomCode();
        Url url = new Url(path, "/" + randomCode);
        urlRepository.save(url);
        log.debug("Generated new shortCode: {} for URL: {}", randomCode, longUrl);
        return createUrl(shortUrlDomain, url.getShortCode());

    }

    /**
     * Retrieves the long URL associated with a given short code.
     *
     * @param shortCode The short code to look up.
     * @return The long URL associated with the short code.
     * @throws UrlNotFoundException if the short code does not exist.
     */
    public String getLongUrl(String shortCode) {

        Optional<Url> existingUrl = urlRepository.findByShortCode(shortCode);
        if (existingUrl.isEmpty()) {
            throw new UrlNotFoundException("URL not found", shortCode);
        }
        Url url = existingUrl.get();
        url.incrementAccessCount(); // Update the access count
        urlRepository.save(url);
        String longUrl = createUrl(longUrlDomain, url.getPath());
        log.info("long url found for: {}, long url: {}", shortCode, longUrl);
        return longUrl;
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
     * Constructs a complete URL from a domain and path.
     *
     * @param domain The domain of the URL.
     * @param path The path of the URL.
     * @return The complete URL.
     */
    private String createUrl(String domain, String path) {
        return protocol + "://" + domain + path;
    }
}
