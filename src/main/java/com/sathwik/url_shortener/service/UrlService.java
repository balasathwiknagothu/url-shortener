package com.sathwik.url_shortener.service;

import com.sathwik.url_shortener.exception.UrlNotFoundException;
import com.sathwik.url_shortener.entity.Url;
import com.sathwik.url_shortener.repository.UrlRepository;
import com.sathwik.url_shortener.util.Base62Encoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    @Transactional
    public Url createShortUrl(String originalUrl) {
        // Step 1: Save with a temporary placeholder to get a generated ID
        Url url = Url.builder()
                .originalUrl(originalUrl)
                .shortCode("temp")
                .build();
        Url saved = urlRepository.save(url);

        // Step 2: Now that we have the DB-generated ID, encode it
        String shortCode = Base62Encoder.encode(saved.getId());
        saved.setShortCode(shortCode);

        return urlRepository.save(saved);
    }

    public Url getByShortCode(String shortCode) {
        return urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No URL found for short code: " + shortCode));
    }
}