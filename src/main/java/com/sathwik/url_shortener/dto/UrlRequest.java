package com.sathwik.url_shortener.dto;

import java.time.LocalDateTime;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UrlRequest {

    @NotBlank(message = "Original URL must not be empty")
    @Pattern(
        regexp = "^(https?://).+",
        message = "URL must start with http:// or https://"
    )
    private String originalUrl;

    @Size(min = 4, max = 20, message = "Custom alias must be between 4 and 20 characters")
    @Pattern(
        regexp = "^[a-zA-Z0-9_-]*$",
        message = "Custom alias can only contain letters, numbers, hyphens, and underscores"
    )
    private String customAlias;

    @Future(message = "Expiration date must be in the future")
    private LocalDateTime expiresAt;
}