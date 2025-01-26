package com.github.melihemreguler.urlshortener.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Component
@Slf4j
public class DatabaseConnectionChecker {

    private final MongoTemplate mongoTemplate;

    // Constructor for dependency injection of MongoTemplate
    @Autowired
    public DatabaseConnectionChecker(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Checks the database connection after the component is initialized.
     * If the connection fails, the application is gracefully shut down.
     */
    @PostConstruct
    public void checkDatabaseConnection() {
        try {
            log.info("Checking database connection...");

            // Sends a ping command to verify the connection to the MongoDB instance
            mongoTemplate.executeCommand("{ ping: 1 }");
            log.info("Database connection successful.");
        } catch (Exception e) {
            log.error("Failed to connect to the database: {}", e.getMessage());

            // Shuts down the application in case of connection failure
            gracefulShutdown();
        }
    }

    /**
     * Gracefully shuts down the application when the database connection fails.
     */
    private void gracefulShutdown() {
        log.warn("Shutting down the application due to database connection failure...");
        System.exit(1);
    }
}
