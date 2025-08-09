package com.example.backend.url_shortener.dto;

import java.time.LocalDateTime;


public class UrlDashboard {
    private String originalUrl;
    private String shortCode;
    private long clickCount;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    // Constructors
    public UrlDashboard(String originalUrl, String shortCode, long clickCount, LocalDateTime createdAt, LocalDateTime expiresAt) {
        this.originalUrl = originalUrl;
        this.shortCode = shortCode;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
    }

    // Getters and setters
    public String getOriginalUrl() {
        return originalUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public long getClickCount() {
        return clickCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
