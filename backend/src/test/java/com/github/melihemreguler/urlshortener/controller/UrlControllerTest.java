package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.dto.UrlDto;
import com.github.melihemreguler.urlshortener.model.PageResponse;
import com.github.melihemreguler.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = UrlController.class)
@ExtendWith(SpringExtension.class)
class UrlControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        // Default behavior in case we don't override it in a specific test
        when(urlService.createAndSaveShortUrl(anyString())).thenReturn("defaultShortUrl");
    }

    // ==================== CREATE SHORT URL TESTS ====================

    @Test
    void createShortUrl_validRequest_shouldReturnShortUrl() throws Exception {
        // GIVEN
        String longUrl = "https://www.google.com";
        String shortUrlMock = "abcd1234";
        given(urlService.createAndSaveShortUrl(longUrl)).willReturn(shortUrlMock);

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"longUrl\":\"" + longUrl + "\"}"));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.shortUrl").value(shortUrlMock));

        then(urlService).should().createAndSaveShortUrl(longUrl);
    }

    @Test
    void createShortUrl_invalidRequest_shouldReturnBadRequest() throws Exception {
        // GIVEN
        String emptyLongUrlJson = "{\"longUrl\":\"\"}"; // This should trigger a validation error.

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emptyLongUrlJson));

        // THEN
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));

        then(urlService).shouldHaveNoInteractions();
    }

    @Test
    void createShortUrl_missingLongUrlField_shouldReturnBadRequest() throws Exception {
        // GIVEN
        String invalidJson = "{}"; // Missing longUrl field

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson));

        // THEN
        resultActions
                .andExpect(status().isBadRequest());

        then(urlService).shouldHaveNoInteractions();
    }

    @Test
    void createShortUrl_nullLongUrl_shouldReturnBadRequest() throws Exception {
        // GIVEN
        String nullLongUrlJson = "{\"longUrl\":null}";

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(nullLongUrlJson));

        // THEN
        resultActions
                .andExpect(status().isBadRequest());

        then(urlService).shouldHaveNoInteractions();
    }

    // ==================== GET ALL SHORT URLS TESTS ====================

    @Test
    void getAllShortUrls_withDefaultPagination_shouldReturnPagedResponse() throws Exception {
        // GIVEN
        List<UrlDto> mockUrls = Arrays.asList(
                createMockUrlDto("1", "https://example1.com", "abc123"),
                createMockUrlDto("2", "https://example2.com", "def456")
        );
        
        PageResponse<UrlDto> mockPageResponse = new PageResponse<>(
                mockUrls, 0, 10, 2, 1, true, true
        );
        
        given(urlService.getAllShortUrls(0, 10)).willReturn(mockPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url"));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].id").value("1"))
                .andExpect(jsonPath("$.content[0].longUrl").value("https://example1.com"))
                .andExpect(jsonPath("$.content[0].shortCode").value("abc123"))
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.first").value(true))
                .andExpect(jsonPath("$.last").value(true));

        then(urlService).should().getAllShortUrls(0, 10);
    }

    @Test
    void getAllShortUrls_withCustomPagination_shouldReturnPagedResponse() throws Exception {
        // GIVEN
        int page = 2;
        int size = 5;
        List<UrlDto> mockUrls = Arrays.asList(
                createMockUrlDto("6", "https://example6.com", "ghi789")
        );
        
        PageResponse<UrlDto> mockPageResponse = new PageResponse<>(
                mockUrls, page, size, 11, 3, false, true
        );
        
        given(urlService.getAllShortUrls(page, size)).willReturn(mockPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url")
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(11))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.first").value(false))
                .andExpect(jsonPath("$.last").value(true));

        then(urlService).should().getAllShortUrls(page, size);
    }

    @Test
    void getAllShortUrls_withEmptyResult_shouldReturnEmptyPage() throws Exception {
        // GIVEN
        PageResponse<UrlDto> emptyPageResponse = new PageResponse<>(
                Collections.emptyList(), 0, 10, 0, 0, true, true
        );
        
        given(urlService.getAllShortUrls(0, 10)).willReturn(emptyPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url"));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0))
                .andExpect(jsonPath("$.totalPages").value(0));

        then(urlService).should().getAllShortUrls(0, 10);
    }

    // ==================== SEARCH URLS TESTS ====================

    @Test
    void searchUrls_withValidSearchTerm_shouldReturnMatchingResults() throws Exception {
        // GIVEN
        String searchTerm = "example";
        List<UrlDto> mockUrls = Arrays.asList(
                createMockUrlDto("1", "https://example.com", "abc123"),
                createMockUrlDto("2", "https://test.com", "example1")
        );
        
        PageResponse<UrlDto> mockPageResponse = new PageResponse<>(
                mockUrls, 0, 10, 2, 1, true, true
        );
        
        given(urlService.searchUrls(searchTerm, 0, 10)).willReturn(mockPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url/search")
                .param("q", searchTerm));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.content[0].longUrl").value("https://example.com"))
                .andExpect(jsonPath("$.content[1].shortCode").value("example1"))
                .andExpect(jsonPath("$.totalElements").value(2));

        then(urlService).should().searchUrls(searchTerm, 0, 10);
    }

    @Test
    void searchUrls_withoutSearchTerm_shouldReturnAllResults() throws Exception {
        // GIVEN
        List<UrlDto> mockUrls = Arrays.asList(
                createMockUrlDto("1", "https://example1.com", "abc123"),
                createMockUrlDto("2", "https://example2.com", "def456")
        );
        
        PageResponse<UrlDto> mockPageResponse = new PageResponse<>(
                mockUrls, 0, 10, 2, 1, true, true
        );
        
        given(urlService.searchUrls(null, 0, 10)).willReturn(mockPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url/search"));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2));

        then(urlService).should().searchUrls(null, 0, 10);
    }

    @Test
    void searchUrls_withCustomPagination_shouldReturnPagedResults() throws Exception {
        // GIVEN
        String searchTerm = "test";
        int page = 1;
        int size = 5;
        List<UrlDto> mockUrls = Arrays.asList(
                createMockUrlDto("6", "https://test6.com", "test789")
        );
        
        PageResponse<UrlDto> mockPageResponse = new PageResponse<>(
                mockUrls, page, size, 6, 2, false, true
        );
        
        given(urlService.searchUrls(searchTerm, page, size)).willReturn(mockPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url/search")
                .param("q", searchTerm)
                .param("page", String.valueOf(page))
                .param("size", String.valueOf(size)));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.page").value(page))
                .andExpect(jsonPath("$.size").value(size))
                .andExpect(jsonPath("$.totalElements").value(6))
                .andExpect(jsonPath("$.totalPages").value(2));

        then(urlService).should().searchUrls(searchTerm, page, size);
    }

    @Test
    void searchUrls_withNoResults_shouldReturnEmptyPage() throws Exception {
        // GIVEN
        String searchTerm = "nonexistent";
        PageResponse<UrlDto> emptyPageResponse = new PageResponse<>(
                Collections.emptyList(), 0, 10, 0, 0, true, true
        );
        
        given(urlService.searchUrls(searchTerm, 0, 10)).willReturn(emptyPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url/search")
                .param("q", searchTerm));

        // THEN
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(0))
                .andExpect(jsonPath("$.totalElements").value(0));

        then(urlService).should().searchUrls(searchTerm, 0, 10);
    }

    // ==================== DELETE SHORT URL TESTS ====================

    @Test
    void deleteShortUrl_withValidId_shouldReturnNoContent() throws Exception {
        // GIVEN
        String urlId = "test-id-123";
        willDoNothing().given(urlService).deleteShortUrl(urlId);

        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/url/{id}", urlId));

        // THEN
        resultActions
                .andExpect(status().isOk()); // Controller returns void, which maps to 200 OK

        then(urlService).should().deleteShortUrl(urlId);
    }

    @Test
    void deleteShortUrl_withSpecialCharactersInId_shouldWork() throws Exception {
        // GIVEN
        String urlId = "test-id-with-special-chars-!@#$%";
        willDoNothing().given(urlService).deleteShortUrl(urlId);

        // WHEN
        ResultActions resultActions = mockMvc.perform(delete("/api/url/{id}", urlId));

        // THEN
        resultActions
                .andExpect(status().isOk());

        then(urlService).should().deleteShortUrl(urlId);
    }

    // ==================== VALIDATION AND ERROR HANDLING TESTS ====================

    @Test
    void createShortUrl_withMalformedJson_shouldReturnBadRequest() throws Exception {
        // GIVEN
        String malformedJson = "{\"longUrl\":\"https://example.com\""; // Missing closing brace

        // WHEN
        ResultActions resultActions = mockMvc.perform(post("/api/url")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson));

        // THEN
        resultActions
                .andExpect(status().isBadRequest());

        then(urlService).shouldHaveNoInteractions();
    }

    @Test
    void getAllShortUrls_withInvalidPageParameter_shouldUseDefault() throws Exception {
        // GIVEN
        PageResponse<UrlDto> mockPageResponse = new PageResponse<>(
                Collections.emptyList(), 0, 10, 0, 0, true, true
        );
        
        given(urlService.getAllShortUrls(0, 10)).willReturn(mockPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url")
                .param("page", "invalid")
                .param("size", "invalid"));

        // THEN
        resultActions
                .andExpect(status().isBadRequest()); // Spring will reject invalid number format
    }

    @Test
    void searchUrls_withNegativePageParameter_shouldWork() throws Exception {
        // GIVEN
        PageResponse<UrlDto> mockPageResponse = new PageResponse<>(
                Collections.emptyList(), 0, 10, 0, 0, true, true
        );
        
        // Service layer should handle negative values, controller just passes them through
        given(urlService.searchUrls("test", -1, 10)).willReturn(mockPageResponse);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/api/url/search")
                .param("q", "test")
                .param("page", "-1"));

        // THEN
        resultActions
                .andExpect(status().isOk());

        then(urlService).should().searchUrls("test", -1, 10);
    }

    // ==================== HELPER METHODS ====================

    private UrlDto createMockUrlDto(String id, String longUrl, String shortCode) {
        UrlDto urlDto = new UrlDto(longUrl, shortCode);
        urlDto.setId(id);
        return urlDto;
    }
}
