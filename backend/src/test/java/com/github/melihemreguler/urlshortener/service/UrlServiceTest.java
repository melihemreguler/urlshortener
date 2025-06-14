package com.github.melihemreguler.urlshortener.service;

import com.github.melihemreguler.urlshortener.config.AppConfig;
import com.github.melihemreguler.urlshortener.dto.UrlDto;
import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import com.github.melihemreguler.urlshortener.model.PageResponse;
import com.github.melihemreguler.urlshortener.repository.UrlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.never;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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

    // ==================== CREATE AND SAVE SHORT URL TESTS ====================

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
    void createAndSaveShortUrl_givenUrlWithWhitespace_whenCalled_thenShouldTrimAndProcess() {
        // GIVEN
        String longUrlWithSpaces = "  https://www.example.com  ";
        String trimmedUrl = "https://www.example.com";
        given(urlRepository.findByLongUrl(trimmedUrl)).willReturn(Optional.empty());

        // WHEN
        String resultShortUrl = urlService.createAndSaveShortUrl(longUrlWithSpaces);

        // THEN
        assertThat(resultShortUrl).startsWith("http://localhost:8080/");
        
        ArgumentCaptor<UrlDto> captor = ArgumentCaptor.forClass(UrlDto.class);
        then(urlRepository).should().save(captor.capture());
        UrlDto savedEntity = captor.getValue();
        assertThat(savedEntity.getLongUrl()).isEqualTo(trimmedUrl);
    }

    @Test
    void createAndSaveShortUrl_givenEmptyUrlAfterTrimming_whenCalled_thenShouldThrowException() {
        // GIVEN
        String emptyUrl = "   ";

        // WHEN & THEN
        assertThatThrownBy(() -> urlService.createAndSaveShortUrl(emptyUrl))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Long URL cannot be empty after trimming");

        then(urlRepository).should(never()).save(any(UrlDto.class));
    }

    // ==================== GET LONG URL TESTS ====================

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

    // ==================== GET ALL SHORT URLS TESTS ====================

    @Test
    void getAllShortUrls_whenCalled_thenShouldReturnAllUrls() {
        // GIVEN
        List<UrlDto> mockUrls = Arrays.asList(
                new UrlDto("https://example.com", "abc123"),
                new UrlDto("https://google.com", "def456")
        );
        given(urlRepository.findAll()).willReturn(mockUrls);

        // WHEN
        List<UrlDto> result = urlService.getAllShortUrls();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result).containsExactlyElementsOf(mockUrls);
        then(urlRepository).should().findAll();
    }

    @Test
    void getAllShortUrls_withPagination_whenCalled_thenShouldReturnPagedResults() {
        // GIVEN
        int page = 1;
        int size = 5;
        List<UrlDto> mockUrls = Arrays.asList(
                new UrlDto("https://example1.com", "abc123"),
                new UrlDto("https://example2.com", "def456"),
                new UrlDto("https://example3.com", "ghi789")
        );
        
        Page<UrlDto> mockPage = new PageImpl<>(mockUrls, PageRequest.of(page, size), 10);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        given(urlRepository.findAll(expectedPageable)).willReturn(mockPage);

        // WHEN
        PageResponse<UrlDto> result = urlService.getAllShortUrls(page, size);

        // THEN
        assertThat(result.getContent()).hasSize(3);
        assertThat(result.getPage()).isEqualTo(1);
        assertThat(result.getSize()).isEqualTo(5);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(2);
        assertThat(result.isFirst()).isFalse();
        assertThat(result.isLast()).isTrue();
        
        then(urlRepository).should().findAll(expectedPageable);
    }

    @Test
    void getAllShortUrls_withPagination_givenEmptyResult_whenCalled_thenShouldReturnEmptyPage() {
        // GIVEN
        int page = 0;
        int size = 10;
        Page<UrlDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        given(urlRepository.findAll(expectedPageable)).willReturn(emptyPage);

        // WHEN
        PageResponse<UrlDto> result = urlService.getAllShortUrls(page, size);

        // THEN
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
        assertThat(result.isFirst()).isTrue();
        assertThat(result.isLast()).isTrue();
    }

    // ==================== DELETE SHORT URL TESTS ====================

    @Test
    void deleteShortUrl_givenValidId_whenCalled_thenShouldCallRepositoryDelete() {
        // GIVEN
        String urlId = "test-id-123";

        // WHEN
        urlService.deleteShortUrl(urlId);

        // THEN
        then(urlRepository).should().deleteById(urlId);
    }

    @Test
    void deleteShortUrl_givenEmptyId_whenCalled_thenShouldStillCallRepositoryDelete() {
        // GIVEN
        String urlId = "";

        // WHEN
        urlService.deleteShortUrl(urlId);

        // THEN
        // Repository should handle empty string gracefully, service just passes it through
        then(urlRepository).should().deleteById(urlId);
    }

    // ==================== SEARCH URLS TESTS ====================

    @Test
    void searchUrls_givenValidSearchTerm_whenCalled_thenShouldReturnMatchingResults() {
        // GIVEN
        String searchTerm = "example";
        int page = 0;
        int size = 10;
        
        List<UrlDto> mockUrls = Arrays.asList(
                new UrlDto("https://example.com", "abc123"),
                new UrlDto("https://test.com", "example1")
        );
        
        Page<UrlDto> mockPage = new PageImpl<>(mockUrls, PageRequest.of(page, size), 2);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        given(urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                searchTerm, expectedPageable)).willReturn(mockPage);

        // WHEN
        PageResponse<UrlDto> result = urlService.searchUrls(searchTerm, page, size);

        // THEN
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getPage()).isEqualTo(0);
        assertThat(result.getSize()).isEqualTo(10);
        
        then(urlRepository).should().findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                searchTerm, expectedPageable);
    }

    @Test
    void searchUrls_givenSearchTermWithWhitespace_whenCalled_thenShouldTrimAndSearch() {
        // GIVEN
        String searchTermWithSpaces = "  example  ";
        String trimmedSearchTerm = "example";
        int page = 0;
        int size = 10;
        
        List<UrlDto> mockUrls = Arrays.asList(new UrlDto("https://example.com", "abc123"));
        Page<UrlDto> mockPage = new PageImpl<>(mockUrls, PageRequest.of(page, size), 1);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        given(urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                trimmedSearchTerm, expectedPageable)).willReturn(mockPage);

        // WHEN
        PageResponse<UrlDto> result = urlService.searchUrls(searchTermWithSpaces, page, size);

        // THEN
        assertThat(result.getContent()).hasSize(1);
        then(urlRepository).should().findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                trimmedSearchTerm, expectedPageable);
    }

    @Test
    void searchUrls_givenNullSearchTerm_whenCalled_thenShouldReturnAllUrls() {
        // GIVEN
        String searchTerm = null;
        int page = 0;
        int size = 10;
        
        List<UrlDto> allUrls = Arrays.asList(
                new UrlDto("https://example.com", "abc123"),
                new UrlDto("https://google.com", "def456")
        );
        Page<UrlDto> mockPage = new PageImpl<>(allUrls, PageRequest.of(page, size), 2);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        given(urlRepository.findAll(expectedPageable)).willReturn(mockPage);

        // WHEN
        PageResponse<UrlDto> result = urlService.searchUrls(searchTerm, page, size);

        // THEN
        assertThat(result.getContent()).hasSize(2);
        then(urlRepository).should().findAll(expectedPageable);
        then(urlRepository).should(never()).findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                any(), any());
    }

    @Test
    void searchUrls_givenEmptySearchTerm_whenCalled_thenShouldReturnAllUrls() {
        // GIVEN
        String searchTerm = "";
        int page = 0;
        int size = 10;
        
        List<UrlDto> allUrls = Arrays.asList(
                new UrlDto("https://example.com", "abc123"),
                new UrlDto("https://google.com", "def456")
        );
        Page<UrlDto> mockPage = new PageImpl<>(allUrls, PageRequest.of(page, size), 2);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        given(urlRepository.findAll(expectedPageable)).willReturn(mockPage);

        // WHEN
        PageResponse<UrlDto> result = urlService.searchUrls(searchTerm, page, size);

        // THEN
        assertThat(result.getContent()).hasSize(2);
        then(urlRepository).should().findAll(expectedPageable);
        then(urlRepository).should(never()).findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                any(), any());
    }

    @Test
    void searchUrls_givenWhitespaceOnlySearchTerm_whenCalled_thenShouldReturnAllUrls() {
        // GIVEN
        String searchTerm = "   ";
        int page = 0;
        int size = 10;
        
        List<UrlDto> allUrls = Arrays.asList(new UrlDto("https://example.com", "abc123"));
        Page<UrlDto> mockPage = new PageImpl<>(allUrls, PageRequest.of(page, size), 1);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        given(urlRepository.findAll(expectedPageable)).willReturn(mockPage);

        // WHEN
        PageResponse<UrlDto> result = urlService.searchUrls(searchTerm, page, size);

        // THEN
        assertThat(result.getContent()).hasSize(1);
        then(urlRepository).should().findAll(expectedPageable);
        then(urlRepository).should(never()).findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                any(), any());
    }

    @Test
    void searchUrls_givenNoMatchingResults_whenCalled_thenShouldReturnEmptyPage() {
        // GIVEN
        String searchTerm = "nonexistent";
        int page = 0;
        int size = 10;
        
        Page<UrlDto> emptyPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(page, size), 0);
        Pageable expectedPageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        given(urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                searchTerm, expectedPageable)).willReturn(emptyPage);

        // WHEN
        PageResponse<UrlDto> result = urlService.searchUrls(searchTerm, page, size);

        // THEN
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getTotalPages()).isEqualTo(0);
    }
}
