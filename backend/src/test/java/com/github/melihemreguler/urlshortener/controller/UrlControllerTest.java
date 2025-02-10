package com.github.melihemreguler.urlshortener.controller;

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

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
}
