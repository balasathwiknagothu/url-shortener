package com.sathwik.url_shortener.controller;

import java.util.List;
import java.util.stream.Collectors;
import com.sathwik.url_shortener.dto.UrlRequest;
import com.sathwik.url_shortener.dto.UrlResponse;
import com.sathwik.url_shortener.entity.Url;
import com.sathwik.url_shortener.service.UrlService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping
    public ResponseEntity<UrlResponse> createShortUrl(@Valid @RequestBody UrlRequest request) {
        Url url = urlService.createShortUrl(request.getOriginalUrl(), request.getCustomAlias(), request.getExpiresAt());

        UrlResponse response = UrlResponse.builder()
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .shortUrl("http://localhost:8080/" + url.getShortCode())
                .clickCount(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{shortCode}")
    public ResponseEntity<UrlResponse> getUrlDetails(@PathVariable String shortCode) {
        Url url = urlService.getByShortCode(shortCode);

        UrlResponse response = UrlResponse.builder()
                .shortCode(url.getShortCode())
                .originalUrl(url.getOriginalUrl())
                .shortUrl("http://localhost:8080/" + url.getShortCode())
                .clickCount(url.getClickCount())
                .createdAt(url.getCreatedAt())
                .expiresAt(url.getExpiresAt())
                .build();

        return ResponseEntity.ok(response);
    }
    @GetMapping("/my-urls")
    public ResponseEntity<List<UrlResponse>> getMyUrls() {
        List<UrlResponse> responses = urlService.getMyUrls().stream()
                .map(url -> UrlResponse.builder()
                        .shortCode(url.getShortCode())
                        .originalUrl(url.getOriginalUrl())
                        .shortUrl("http://localhost:8080/" + url.getShortCode())
                        .clickCount(url.getClickCount())
                        .createdAt(url.getCreatedAt())
                        .expiresAt(url.getExpiresAt())
                        .build())
                .collect(Collectors.toList());
    
        return ResponseEntity.ok(responses);
    }
    
    @DeleteMapping("/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}