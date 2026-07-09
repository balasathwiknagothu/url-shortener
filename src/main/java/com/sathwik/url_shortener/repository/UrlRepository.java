package com.sathwik.url_shortener.repository;

import com.sathwik.url_shortener.entity.Url;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UrlRepository extends JpaRepository<Url, Long> {
    Optional<Url> findByShortCode(String shortCode);
    boolean existsByShortCode(String shortCode);
    List<Url> findByUserId(Long userId);
}