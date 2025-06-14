package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.exception.UrlNotFoundException;
import com.github.melihemreguler.urlshortener.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

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

        Map<String, String> errors = new HashMap<>();
        
        // Collect field errors, handling duplicates by taking the first error message
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String fieldName = fieldError.getField();
            if (!errors.containsKey(fieldName)) {
                errors.put(fieldName, fieldError.getDefaultMessage());
            }
        }

        errors.put("error", "Validation failed");
        return errors;
    }

    /**
     * Handles JSON parse errors (malformed JSON).
     *
     * @param ex The exception containing details about the JSON parsing error.
     * @param request The HTTP request.
     * @return A JSON response indicating the malformed JSON error.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("JSON parse error: {}", ex.getMessage());
        return ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                "Invalid JSON format",
                request.getRequestURI()
        );
    }

    /**
     * Handles method argument type mismatch errors (e.g., invalid parameter types).
     *
     * @param ex The exception containing details about the type mismatch.
     * @param request The HTTP request.
     * @return A JSON response indicating the parameter type error.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        log.warn("Parameter type mismatch: {} for parameter '{}'", ex.getValue(), ex.getName());
        return ErrorResponse.of(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName()),
                request.getRequestURI()
        );
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

    /**
     * Handles 404 Not Found errors for unmapped endpoints.
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        log.warn("404 Not Found: {} {}", ex.getHttpMethod(), ex.getRequestURL());
        return ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "The requested resource was not found",
                request.getRequestURI()
        );
    }

    /**
     * Handles static resource not found errors.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public ErrorResponse handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.warn("Static resource not found: {}", ex.getResourcePath());
        return ErrorResponse.of(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                "The requested resource was not found",
                request.getRequestURI()
        );
    }

    /**
     * Handles all other uncaught exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public ErrorResponse handleGenericException(Exception ex, HttpServletRequest request) {
        log.error("Unexpected error occurred", ex);
        return ErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred",
                request.getRequestURI()
        );
    }
}
