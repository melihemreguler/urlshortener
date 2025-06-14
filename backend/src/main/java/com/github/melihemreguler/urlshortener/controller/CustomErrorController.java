package com.github.melihemreguler.urlshortener.controller;

import com.github.melihemreguler.urlshortener.model.ErrorResponse;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Hidden
@RestController
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public ResponseEntity<ErrorResponse> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String requestUri = (String) request.getAttribute("javax.servlet.error.request_uri");
        
        if (statusCode == null) {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }
        
        if (requestUri == null) {
            requestUri = "unknown";
        }
        
        HttpStatus status = HttpStatus.valueOf(statusCode);
        String message = switch (statusCode) {
            case 404 -> "The requested resource was not found";
            case 405 -> "Method not allowed";
            case 500 -> "Internal server error";
            default -> "An error occurred";
        };
        
        log.warn("Error handled: {} {} - {}", statusCode, requestUri, message);
        
        ErrorResponse errorResponse = ErrorResponse.of(
                statusCode,
                status.getReasonPhrase(),
                message,
                requestUri
        );
        
        return ResponseEntity.status(status).body(errorResponse);
    }
}
