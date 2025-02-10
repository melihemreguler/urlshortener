package com.github.melihemreguler.urlshortener.repository;

import com.github.melihemreguler.urlshortener.dto.UrlDto;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<UrlDto, String> {
    Optional<UrlDto> findByLongUrl(String longUrl);
    Optional<UrlDto> findByShortCode(String code);
}
