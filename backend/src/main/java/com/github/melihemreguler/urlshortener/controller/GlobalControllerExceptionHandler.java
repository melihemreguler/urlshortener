package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    /**
     * Handles missing request parameter exceptions.
     *
     * @param ex The exception containing details about the missing parameter.
     * @return A bad request response indicating the missing parameter.
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        log.warn("Missing required parameter: {}", paramName);
        return ResponseEntity.badRequest().body("Missing required parameter: " + paramName);
    }

    /**
     * Handles cases where a requested short URL does not exist.
     *
     * @param ex The exception containing details about the missing URL.
     * @return A not found response.
     */
    @ExceptionHandler(UrlNotFoundException.class)
    public ResponseEntity<String> handleContentNotFound(UrlNotFoundException ex){
        String url = ex.getUrl();
        log.warn("The requested short URL does not exist in the database. url: {}", url);
        return ResponseEntity.notFound().build();
    }

}
