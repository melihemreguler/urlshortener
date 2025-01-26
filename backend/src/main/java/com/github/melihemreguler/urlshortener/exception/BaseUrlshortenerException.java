package com.github.melihemreguler.urlshortener.exception;


import lombok.Getter;

@Getter
public class BaseUrlshortenerException extends RuntimeException {

    private final String details;  // Additional details about the exception


    public BaseUrlshortenerException(String message) {
        super(message);
        this.details = null;
    }

    public BaseUrlshortenerException(String message, Throwable cause) {
        super(message, cause);
        this.details = null;
    }


    public BaseUrlshortenerException(String message, String details) {
        super(message);
        this.details = details;
    }

    public BaseUrlshortenerException(String message, Throwable cause, String details) {
        super(message, cause);
        this.details = details;
    }

    @Override
    public String toString() {
        return "BaseUrlshortenerException{" +
                "details='" + details + '\'' +
                ", message='" + getMessage() + '\'' +
                ", cause=" + getCause() +
                '}';
    }
}