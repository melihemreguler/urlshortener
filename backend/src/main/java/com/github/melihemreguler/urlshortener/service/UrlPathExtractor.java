package com.github.melihemreguler.urlshortener.service;

import java.net.MalformedURLException;
import java.net.URL;

public class UrlPathExtractor {

    /**
     * Extracts the path from the given URL string.
     *
     * @param urlString The full URL as a string.
     * @return The path component of the URL.
     * @throws IllegalArgumentException if the given string is not a valid URL.
     */
    public static String extractPath(String urlString) {
        try {
            // Creates a URL object to parse the provided string
            URL url = new URL(urlString);

            // Extracts and returns the path
            return url.getPath();
        } catch (MalformedURLException e) {
            // Throws an exception if the URL is invalid
            throw new IllegalArgumentException("Invalid URL: " + urlString, e);
        }
    }
}

