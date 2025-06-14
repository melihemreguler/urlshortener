package com.github.melihemreguler.urlshortener.repository;

import com.github.melihemreguler.urlshortener.dto.UrlDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for UrlRepository using embedded MongoDB.
 * Tests the custom query methods and pagination functionality.
 */
@DataMongoTest
@TestPropertySource(properties = {
        "spring.profiles.active=test"
})
class UrlRepositoryTest {

    @Autowired
    private UrlRepository urlRepository;

    @BeforeEach
    void setUp() {
        // Clean up the repository before each test
        urlRepository.deleteAll();
    }

    // ==================== BASIC CRUD TESTS ====================

    @Test
    void save_givenValidUrlDto_whenSaved_thenShouldPersistSuccessfully() {
        // GIVEN
        UrlDto urlDto = new UrlDto("https://example.com", "abc123");

        // WHEN
        UrlDto savedUrl = urlRepository.save(urlDto);

        // THEN
        assertThat(savedUrl).isNotNull();
        assertThat(savedUrl.getId()).isNotNull();
        assertThat(savedUrl.getLongUrl()).isEqualTo("https://example.com");
        assertThat(savedUrl.getShortCode()).isEqualTo("abc123");
        assertThat(savedUrl.getCreatedAt()).isNotNull();
        assertThat(savedUrl.getAccessCount()).isEqualTo(0);
    }

    @Test
    void findById_givenExistingId_whenFound_thenShouldReturnUrlDto() {
        // GIVEN
        UrlDto urlDto = new UrlDto("https://example.com", "abc123");
        UrlDto savedUrl = urlRepository.save(urlDto);

        // WHEN
        Optional<UrlDto> foundUrl = urlRepository.findById(savedUrl.getId());

        // THEN
        assertThat(foundUrl).isPresent();
        assertThat(foundUrl.get().getLongUrl()).isEqualTo("https://example.com");
        assertThat(foundUrl.get().getShortCode()).isEqualTo("abc123");
    }

    @Test
    void findById_givenNonExistentId_whenSearched_thenShouldReturnEmpty() {
        // GIVEN
        String nonExistentId = "non-existent-id";

        // WHEN
        Optional<UrlDto> foundUrl = urlRepository.findById(nonExistentId);

        // THEN
        assertThat(foundUrl).isEmpty();
    }

    // ==================== CUSTOM QUERY TESTS ====================

    @Test
    void findByLongUrl_givenExistingLongUrl_whenSearched_thenShouldReturnUrlDto() {
        // GIVEN
        String longUrl = "https://example.com";
        UrlDto urlDto = new UrlDto(longUrl, "abc123");
        urlRepository.save(urlDto);

        // WHEN
        Optional<UrlDto> foundUrl = urlRepository.findByLongUrl(longUrl);

        // THEN
        assertThat(foundUrl).isPresent();
        assertThat(foundUrl.get().getLongUrl()).isEqualTo(longUrl);
        assertThat(foundUrl.get().getShortCode()).isEqualTo("abc123");
    }

    @Test
    void findByLongUrl_givenNonExistentLongUrl_whenSearched_thenShouldReturnEmpty() {
        // GIVEN
        String nonExistentUrl = "https://nonexistent.com";

        // WHEN
        Optional<UrlDto> foundUrl = urlRepository.findByLongUrl(nonExistentUrl);

        // THEN
        assertThat(foundUrl).isEmpty();
    }

    @Test
    void findByShortCode_givenExistingShortCode_whenSearched_thenShouldReturnUrlDto() {
        // GIVEN
        String shortCode = "abc123";
        UrlDto urlDto = new UrlDto("https://example.com", shortCode);
        urlRepository.save(urlDto);

        // WHEN
        Optional<UrlDto> foundUrl = urlRepository.findByShortCode(shortCode);

        // THEN
        assertThat(foundUrl).isPresent();
        assertThat(foundUrl.get().getShortCode()).isEqualTo(shortCode);
        assertThat(foundUrl.get().getLongUrl()).isEqualTo("https://example.com");
    }

    @Test
    void findByShortCode_givenNonExistentShortCode_whenSearched_thenShouldReturnEmpty() {
        // GIVEN
        String nonExistentShortCode = "nonexistent";

        // WHEN
        Optional<UrlDto> foundUrl = urlRepository.findByShortCode(nonExistentShortCode);

        // THEN
        assertThat(foundUrl).isEmpty();
    }

    // ==================== SEARCH QUERY TESTS ====================

    @Test
    void findByLongUrlContaining_givenMatchingLongUrl_whenSearched_thenShouldReturnResults() {
        // GIVEN
        urlRepository.save(new UrlDto("https://example.com", "abc123"));
        urlRepository.save(new UrlDto("https://google.com", "def456"));
        urlRepository.save(new UrlDto("https://example.org", "ghi789"));
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // WHEN
        Page<UrlDto> results = urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                "example", pageable);

        // THEN
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent())
                .extracting(UrlDto::getLongUrl)
                .containsExactlyInAnyOrder("https://example.com", "https://example.org");
    }

    @Test
    void findByShortCodeContaining_givenMatchingShortCode_whenSearched_thenShouldReturnResults() {
        // GIVEN
        urlRepository.save(new UrlDto("https://example.com", "test123"));
        urlRepository.save(new UrlDto("https://google.com", "abc456"));
        urlRepository.save(new UrlDto("https://demo.com", "test789"));
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // WHEN
        Page<UrlDto> results = urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                "test", pageable);

        // THEN
        assertThat(results.getContent()).hasSize(2);
        assertThat(results.getContent())
                .extracting(UrlDto::getShortCode)
                .containsExactlyInAnyOrder("test123", "test789");
    }

    @Test
    void findByCombinedSearch_givenMatchingBothFields_whenSearched_thenShouldReturnAllMatches() {
        // GIVEN
        urlRepository.save(new UrlDto("https://example.com", "abc123"));  // matches longUrl
        urlRepository.save(new UrlDto("https://google.com", "example"));  // matches shortCode
        urlRepository.save(new UrlDto("https://demo.com", "xyz789"));     // no match
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // WHEN
        Page<UrlDto> results = urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                "example", pageable);

        // THEN
        assertThat(results.getContent()).hasSize(2);
        List<String> longUrls = results.getContent().stream()
                .map(UrlDto::getLongUrl)
                .toList();
        assertThat(longUrls).containsExactlyInAnyOrder("https://example.com", "https://google.com");
    }

    @Test
    void findByCaseInsensitiveSearch_givenMixedCaseSearch_whenSearched_thenShouldReturnResults() {
        // GIVEN
        urlRepository.save(new UrlDto("https://EXAMPLE.com", "ABC123"));
        urlRepository.save(new UrlDto("https://google.com", "example"));
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // WHEN
        Page<UrlDto> results = urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                "Example", pageable);

        // THEN
        assertThat(results.getContent()).hasSize(2);
    }

    @Test
    void findBySearch_givenNoMatches_whenSearched_thenShouldReturnEmptyPage() {
        // GIVEN
        urlRepository.save(new UrlDto("https://example.com", "abc123"));
        urlRepository.save(new UrlDto("https://google.com", "def456"));
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // WHEN
        Page<UrlDto> results = urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                "nonexistent", pageable);

        // THEN
        assertThat(results.getContent()).isEmpty();
        assertThat(results.getTotalElements()).isEqualTo(0);
        assertThat(results.getTotalPages()).isEqualTo(0);
    }

    // ==================== PAGINATION TESTS ====================

    @Test
    void findAll_withPagination_whenRequested_thenShouldReturnPagedResults() {
        // GIVEN
        // Create 15 URLs to test pagination
        for (int i = 1; i <= 15; i++) {
            urlRepository.save(new UrlDto("https://example" + i + ".com", "code" + i));
        }
        
        Pageable firstPage = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Pageable secondPage = PageRequest.of(1, 5, Sort.by("createdAt").descending());

        // WHEN
        Page<UrlDto> firstPageResult = urlRepository.findAll(firstPage);
        Page<UrlDto> secondPageResult = urlRepository.findAll(secondPage);

        // THEN
        assertThat(firstPageResult.getContent()).hasSize(5);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(15);
        assertThat(firstPageResult.getTotalPages()).isEqualTo(3);
        assertThat(firstPageResult.isFirst()).isTrue();
        assertThat(firstPageResult.isLast()).isFalse();

        assertThat(secondPageResult.getContent()).hasSize(5);
        assertThat(secondPageResult.getTotalElements()).isEqualTo(15);
        assertThat(secondPageResult.getTotalPages()).isEqualTo(3);
        assertThat(secondPageResult.isFirst()).isFalse();
        assertThat(secondPageResult.isLast()).isFalse();
    }

    @Test
    void findBySearch_withPagination_whenRequested_thenShouldReturnPagedResults() {
        // GIVEN
        // Create URLs where some match the search term
        for (int i = 1; i <= 8; i++) {
            if (i % 2 == 0) {
                urlRepository.save(new UrlDto("https://example" + i + ".com", "code" + i));
            } else {
                urlRepository.save(new UrlDto("https://test" + i + ".com", "code" + i));
            }
        }
        
        Pageable firstPage = PageRequest.of(0, 2, Sort.by("createdAt").descending());
        Pageable secondPage = PageRequest.of(1, 2, Sort.by("createdAt").descending());

        // WHEN
        Page<UrlDto> firstPageResult = urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                "example", firstPage);
        Page<UrlDto> secondPageResult = urlRepository.findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
                "example", secondPage);

        // THEN
        assertThat(firstPageResult.getContent()).hasSize(2);
        assertThat(firstPageResult.getTotalElements()).isEqualTo(4); // 4 URLs contain "example"
        assertThat(firstPageResult.getTotalPages()).isEqualTo(2);
        assertThat(firstPageResult.isFirst()).isTrue();
        assertThat(firstPageResult.isLast()).isFalse();

        assertThat(secondPageResult.getContent()).hasSize(2);
        assertThat(secondPageResult.isFirst()).isFalse();
        assertThat(secondPageResult.isLast()).isTrue();
    }

    // ==================== DELETE TESTS ====================

    @Test
    void deleteById_givenExistingId_whenDeleted_thenShouldRemoveFromDatabase() {
        // GIVEN
        UrlDto urlDto = new UrlDto("https://example.com", "abc123");
        UrlDto savedUrl = urlRepository.save(urlDto);
        String savedId = savedUrl.getId();

        // WHEN
        urlRepository.deleteById(savedId);

        // THEN
        Optional<UrlDto> deletedUrl = urlRepository.findById(savedId);
        assertThat(deletedUrl).isEmpty();
    }

    @Test
    void deleteById_givenNonExistentId_whenDeleted_thenShouldNotThrowException() {
        // GIVEN
        String nonExistentId = "non-existent-id";

        // WHEN & THEN
        assertThatNoException().isThrownBy(() -> urlRepository.deleteById(nonExistentId));
    }

    // ==================== SORTING TESTS ====================

    @Test
    void findAll_withCreatedAtDescendingSort_whenRequested_thenShouldReturnSortedResults() throws InterruptedException {
        // GIVEN
        urlRepository.save(new UrlDto("https://first.com", "first"));
        Thread.sleep(1); // Ensure different timestamps
        urlRepository.save(new UrlDto("https://second.com", "second"));
        Thread.sleep(1);
        urlRepository.save(new UrlDto("https://third.com", "third"));
        
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());

        // WHEN
        Page<UrlDto> result = urlRepository.findAll(pageable);

        // THEN
        assertThat(result.getContent()).hasSize(3);
        // Most recent should be first (descending order)
        assertThat(result.getContent().get(0).getShortCode()).isEqualTo("third");
        assertThat(result.getContent().get(1).getShortCode()).isEqualTo("second");
        assertThat(result.getContent().get(2).getShortCode()).isEqualTo("first");
    }
}
