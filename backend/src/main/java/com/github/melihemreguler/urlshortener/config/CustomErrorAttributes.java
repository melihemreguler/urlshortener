package com.github.melihemreguler.urlshortener.config;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Component
public class CustomErrorAttributes extends DefaultErrorAttributes {

    @Override
    public Map<String, Object> getErrorAttributes(WebRequest webRequest, ErrorAttributeOptions options) {
        // Create completely custom error response without traces
        Map<String, Object> customAttributes = new java.util.HashMap<>();
        
        // Get status from request
        Integer status = (Integer) webRequest.getAttribute("javax.servlet.error.status_code", WebRequest.SCOPE_REQUEST);
        String path = (String) webRequest.getAttribute("javax.servlet.error.request_uri", WebRequest.SCOPE_REQUEST);
        
        if (status == null) status = 500;
        if (path == null) path = "unknown";
        
        // Custom clean response
        customAttributes.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        customAttributes.put("status", status);
        customAttributes.put("error", getErrorName(status));
        customAttributes.put("message", getErrorMessage(status));
        customAttributes.put("path", path);
        
        return customAttributes;
    }
    
    private String getErrorName(int status) {
        return switch (status) {
            case 404 -> "Not Found";
            case 405 -> "Method Not Allowed";
            case 500 -> "Internal Server Error";
            default -> "Error";
        };
    }
    
    private String getErrorMessage(int status) {
        return switch (status) {
            case 404 -> "The requested resource was not found";
            case 405 -> "Method not allowed";
            case 500 -> "Internal server error";
            default -> "An error occurred";
        };
    }
}
