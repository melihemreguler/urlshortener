package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Hidden
@ControllerAdvice
public class GlobalControllerExceptionHandler {

    /**
     * Handles validation errors when request body is invalid.
     *
     * @param ex The exception containing details about the validation errors.
     * @return A JSON response with validation error messages.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Validation error: {}", ex.getBindingResult().getAllErrors());

        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        DefaultMessageSourceResolvable::getDefaultMessage
                ));

        errors.put("error", "Validation failed");
        return errors;
    }

    /**
     * Handles cases where a requested short URL does not exist.
     *
     * @param ex The exception containing details about the missing URL.
     * @return A JSON response indicating the missing URL.
     */
    @ExceptionHandler(UrlNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public Map<String, String> handleContentNotFound(UrlNotFoundException ex) {
        String url = ex.getUrl();
        log.warn("The requested short URL does not exist in the database. url: {}", url);

        Map<String, String> response = new HashMap<>();
        response.put("error", "Short code not found");
        response.put("url", url);
        return response;
    }
}
