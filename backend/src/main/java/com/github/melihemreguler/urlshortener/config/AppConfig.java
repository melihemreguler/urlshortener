package com.github.melihemreguler.urlshortener.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "service.config")
@Getter
@Setter
public class AppConfig {
    private String serviceUrl;
}
