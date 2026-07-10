package com.sathwik.url_shortener.service;

import com.sathwik.url_shortener.repository.ClickEventRepository;
import com.sathwik.url_shortener.entity.ClickEvent;
import com.sathwik.url_shortener.exception.UrlExpiredException;
import java.time.LocalDateTime;
import com.sathwik.url_shortener.exception.AliasAlreadyExistsException;
import com.sathwik.url_shortener.entity.User;
import com.sathwik.url_shortener.exception.UnauthorizedAccessException;
import com.sathwik.url_shortener.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.List;
import com.sathwik.url_shortener.exception.UrlNotFoundException;
import com.sathwik.url_shortener.entity.Url;
import com.sathwik.url_shortener.repository.UrlRepository;
import com.sathwik.url_shortener.util.Base62Encoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final UserRepository userRepository;
    private final ClickEventRepository clickEventRepository;

    public UrlService(UrlRepository urlRepository, UserRepository userRepository, ClickEventRepository clickEventRepository) {
        this.urlRepository = urlRepository;
        this.userRepository = userRepository;
        this.clickEventRepository = clickEventRepository;
    }

    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UnauthorizedAccessException("Authenticated user not found"));
    }

    @Transactional
    public Url createShortUrl(String originalUrl, String customAlias, LocalDateTime expiresAt) {
        User currentUser = getCurrentUser();

        if (customAlias != null && !customAlias.isBlank()) {
            if (urlRepository.existsByShortCode(customAlias)) {
                throw new AliasAlreadyExistsException("Alias already taken: " + customAlias);
            }

            Url url = Url.builder()
                    .originalUrl(originalUrl)
                    .shortCode(customAlias)
                    .user(currentUser)
                    .expiresAt(expiresAt)
                    .build();

            return urlRepository.save(url);
        }

        Url url = Url.builder()
                .originalUrl(originalUrl)
                .shortCode("temp")
                .user(currentUser)
                .expiresAt(expiresAt)
                .build();
        Url saved = urlRepository.save(url);

        String shortCode = Base62Encoder.encode(saved.getId());
        saved.setShortCode(shortCode);

        return urlRepository.save(saved);
    }

    public Url getByShortCode(String shortCode) {
        Url url = urlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new UrlNotFoundException("No URL found for short code: " + shortCode));

        if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new UrlExpiredException("This short URL has expired: " + shortCode);
        }

        return url;
    }
    public List<Url> getMyUrls() {
        User currentUser = getCurrentUser();
        return urlRepository.findByUserId(currentUser.getId());
    }
    
    @Transactional
    public void deleteUrl(String shortCode) {
        Url url = getByShortCode(shortCode);
        User currentUser = getCurrentUser();
    
        if (!url.getUser().getId().equals(currentUser.getId())) {
            throw new UnauthorizedAccessException("You do not have permission to delete this URL");
        }
    
        urlRepository.delete(url);
    }
    @Transactional
    public String recordClickAndGetOriginalUrl(String shortCode) {
        Url url = getByShortCode(shortCode); // expiration check happens here automatically

        url.setClickCount(url.getClickCount() + 1);
        urlRepository.save(url);

        ClickEvent event = ClickEvent.builder().url(url).build();
        clickEventRepository.save(event);

        return url.getOriginalUrl();
    }
}