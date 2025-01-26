package com.github.melihemreguler.urlshortener.repository;

import com.github.melihemreguler.urlshortener.model.Url;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UrlRepository extends MongoRepository<Url, String> {
    Optional<Url> findByPath(String url);
    Optional<Url> findByShortCode(String code);
}
