package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import com.github.melihemreguler.urlshortener.service.UrlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RedirectController.class)
@ExtendWith(SpringExtension.class)
class RedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        // Default behavior in case no specific stubbing is done in a test
        when(urlService.getLongUrl(anyString())).thenReturn("https://www.default.com");
    }

    @Test
    void redirectToLongUrl_validShortCode_shouldRedirect() throws Exception {
        // GIVEN
        String shortCode = "abcd1234";
        String longUrl = "https://www.google.com";
        given(urlService.getLongUrl(shortCode)).willReturn(longUrl);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/" + shortCode));

        // THEN
        resultActions
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(longUrl));

        then(urlService).should().getLongUrl(shortCode);
    }

    @Test
    void redirectToLongUrl_invalidShortCode_shouldReturnNotFound() throws Exception {
        // GIVEN
        String shortCode = "invalidShort";
        doThrow(new UrlNotFoundException("Short code not found", shortCode))
                .when(urlService).getLongUrl(shortCode);

        // WHEN
        ResultActions resultActions = mockMvc.perform(get("/" + shortCode));

        // THEN
        resultActions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Short code not found"))
                .andExpect(jsonPath("$.url").value(shortCode));

        then(urlService).should().getLongUrl(shortCode);
    }
}
