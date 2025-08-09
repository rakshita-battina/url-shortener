package com.example.backend.url_shortener.controller;

import com.example.backend.url_shortener.dto.ShortenRequest;
import com.example.backend.url_shortener.dto.UrlDashboard;
import com.example.backend.url_shortener.model.ShortUrl;
import com.example.backend.url_shortener.service.UrlService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class UrlController {

    private final UrlService urlService;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }

    @PostMapping("/api/shorten")
public ResponseEntity<Map<String, String>> shortenUrl(@RequestBody ShortenRequest request) {
    try {
        System.out.println("Received request: " + request.getOriginalUrl() + ", Custom Code: " + request.getCustomCode() + ", Expires At: " + request.getExpiresAt());
        ShortUrl shortUrl = urlService.createShortUrl(
            request.getOriginalUrl(),
            request.getCustomCode(), // ✅ use customCode
            request.getExpiresAt()   // ✅ support expiry
        );

        return ResponseEntity.ok(Map.of(
            "shortCode", shortUrl.getShortCode(),
            "originalUrl", shortUrl.getOriginalUrl()
        ));
    } catch (IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
    }
}

   

    @GetMapping("/r/{shortCode}")
    public ResponseEntity<?> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        Optional<ShortUrl> result = urlService.getOriginalUrl(shortCode);
        if (result.isPresent()) {
            ShortUrl shortUrl = result.get();
            urlService.recordClick(shortUrl, request.getRemoteAddr());
            return ResponseEntity.status(302).location(URI.create(shortUrl.getOriginalUrl())).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/analytics/{shortCode}")
    public ResponseEntity<?> analytics(@PathVariable String shortCode) {
        Optional<ShortUrl> result = urlService.getOriginalUrl(shortCode);
        if (result.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        ShortUrl shortUrl = result.get();
        long clickCount = urlService.getClickCount(shortUrl);
        return ResponseEntity.ok(Map.of("shortCode", shortCode, "clickCount", clickCount));
    }

    @GetMapping("/api/urls")
public ResponseEntity<List<UrlDashboard>> getAllUrls() {
    List<UrlDashboard> urls = urlService.getUrlsWithClickCount();
    return ResponseEntity.ok(urls);
}
    @DeleteMapping("/api/url/{shortCode}")
    public ResponseEntity<Void> deleteUrl(@PathVariable String shortCode) {
        urlService.deleteByShortCode(shortCode);
        return ResponseEntity.noContent().build();
    }
        
    
}