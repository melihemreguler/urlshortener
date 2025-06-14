package com.github.melihemreguler.urlshortener.repository;

import com.github.melihemreguler.urlshortener.dto.UrlDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<UrlDto, String> {
    Optional<UrlDto> findByLongUrl(String longUrl);
    Optional<UrlDto> findByShortCode(String code);
    
    // Search in both longUrl and shortCode fields with case-insensitive regex
    @Query("{ $or: [ " +
           "{ 'longUrl': { $regex: ?0, $options: 'i' } }, " +
           "{ 'shortCode': { $regex: ?0, $options: 'i' } } " +
           "] }")
    Page<UrlDto> findByLongUrlContainingIgnoreCaseOrShortCodeContainingIgnoreCase(
        String searchTerm, Pageable pageable);
}
