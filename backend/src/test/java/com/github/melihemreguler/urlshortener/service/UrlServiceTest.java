package com.github.melihemreguler.urlshortener.service;

import com.github.melihemreguler.urlshortener.config.AppConfig;
import com.github.melihemreguler.urlshortener.dto.UrlDto;
import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import com.github.melihemreguler.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

/**
 * Unit tests for the UrlService class using a BDD (Given-When-Then) style approach.
 * We do not directly test private methods like generateRandomCode;
 * only the public methods' functional behavior is verified.
 */
@ExtendWith(SpringExtension.class)
class UrlServiceTest {

    @Mock
    private UrlRepository urlRepository;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        // Default (mock) service URL for all tests
        given(appConfig.getServiceUrl()).willReturn("http://localhost:8080");
    }

    @Test
    void createAndSaveShortUrl_givenUrlAlreadyExists_whenCalled_thenShouldReturnExistingShortUrl() {
        // GIVEN
        String longUrl = "https://www.google.com";
        String existingShortCode = "abc12345";
        UrlDto existingUrlDto = new UrlDto(longUrl, existingShortCode);
        given(urlRepository.findByLongUrl(longUrl)).willReturn(Optional.of(existingUrlDto));

        // WHEN
        String resultShortUrl = urlService.createAndSaveShortUrl(longUrl);

        // THEN
        // Should return the existing short URL (with the base service URL prefix)
        assertThat(resultShortUrl).isEqualTo("http://localhost:8080/" + existingShortCode);

        // No new UrlDto should be saved in the repository, because it already exists
        then(urlRepository).should(never()).save(any(UrlDto.class));
    }

    @Test
    void createAndSaveShortUrl_givenUrlNotFound_whenCalled_thenShouldGenerateAndReturnNewShortUrl() {
        // GIVEN
        String longUrl = "https://www.example.com";
        given(urlRepository.findByLongUrl(longUrl)).willReturn(Optional.empty());

        // WHEN
        String generatedShortUrl = urlService.createAndSaveShortUrl(longUrl);

        // THEN
        // We don't test the exact random code (private method),
        // but we confirm a new short URL with prefix was returned
        assertThat(generatedShortUrl)
                .startsWith("http://localhost:8080/")
                .hasSizeGreaterThan("http://localhost:8080/".length());

        // Verify a new UrlDto was indeed saved
        ArgumentCaptor<UrlDto> captor = ArgumentCaptor.forClass(UrlDto.class);
        then(urlRepository).should().save(captor.capture());
        UrlDto savedEntity = captor.getValue();

        // The saved entity should match our longUrl,
        // and have a non-empty shortCode (because a new one was generated)
        assertThat(savedEntity.getLongUrl()).isEqualTo(longUrl);
        assertThat(savedEntity.getShortCode()).isNotBlank();
    }

    @Test
    void getLongUrl_givenShortUrlExists_whenCalled_thenShouldIncrementAccessCountAndReturnLongUrl() {
        // GIVEN
        String shortCode = "abc12345";
        String longUrl = "https://www.google.com";
        UrlDto existingUrlDto = new UrlDto(longUrl, shortCode);
        existingUrlDto.setAccessCount(5); // Suppose it was already accessed 5 times
        given(urlRepository.findByShortCode(shortCode)).willReturn(Optional.of(existingUrlDto));

        // WHEN
        String actualLongUrl = urlService.getLongUrl(shortCode);

        // THEN
        assertThat(actualLongUrl).isEqualTo(longUrl);

        // Access count should be incremented
        assertThat(existingUrlDto.getAccessCount()).isEqualTo(6);

        // And the updated entity should be saved
        then(urlRepository).should().save(existingUrlDto);
    }

    @Test
    void getLongUrl_givenShortUrlNotFound_whenCalled_thenShouldThrowUrlNotFoundException() {
        // GIVEN
        String missingShortCode = "nonExistent123";
        given(urlRepository.findByShortCode(missingShortCode)).willReturn(Optional.empty());

        // WHEN & THEN
        assertThatThrownBy(() -> urlService.getLongUrl(missingShortCode))
                .isInstanceOf(UrlNotFoundException.class)
                .hasMessageContaining("URL not found");  // from the exception's constructor

        then(urlRepository).should(never()).save(any(UrlDto.class));
    }
}
