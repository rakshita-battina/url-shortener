package com.example.backend.url_shortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Click {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime timestamp;
    private String ipAddress;

    @ManyToOne
    private ShortUrl shortUrl;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public ShortUrl getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(ShortUrl shortUrl) {
        this.shortUrl = shortUrl;
    }

    // Getters and Setters
    

    
}
