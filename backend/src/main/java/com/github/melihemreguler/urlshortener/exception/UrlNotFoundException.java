package com.github.melihemreguler.urlshortener.exception;

import lombok.Getter;

@Getter
public class UrlNotFoundException extends BaseUrlshortenerException {

    // The URL that caused the exception
    private final String url;

    public UrlNotFoundException(String message, String url) {
        super(message);
        this.url = url;
    }

    public UrlNotFoundException(String message, Throwable cause, String url) {
        super(message, cause);
        this.url = url;
    }

    public UrlNotFoundException(String message, String details, String url) {
        super(message, details);
        this.url = url;
    }

    public UrlNotFoundException(String message, Throwable cause, String details, String url) {
        super(message, cause, details);
        this.url = url;
    }

}
