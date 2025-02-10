package com.github.melihemreguler.urlshortener.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UrlRequest(
        @NotNull(message = "Long URL must not be null")
        @NotBlank(message = "Long URL cannot be empty")
        String longUrl
) {
}