package com.example.backend.url_shortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class ShortUrl {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalUrl;
    private String shortCode;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt; // optional expiration time
    private long clickCount = 0;
    @OneToMany(mappedBy = "shortUrl", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Click> clicks = new ArrayList<>();

    public LocalDateTime getExpiresAt() {
        
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    

    public long getClickCount() {
        return clickCount;
    }
    public void setClickCount(long clickCount) {
        this.clickCount = clickCount;
    }
    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getOriginalUrl() {
        return originalUrl;
    }
    public void setOriginalUrl(String originalUrl) {
        this.originalUrl = originalUrl;
    }
    public String getShortCode() {
        return shortCode;
    }
    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Getters and Setters
    
}
