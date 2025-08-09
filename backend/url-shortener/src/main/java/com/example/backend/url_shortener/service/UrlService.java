package com.example.backend.url_shortener.service;

import com.example.backend.url_shortener.dto.UrlDashboard;
import com.example.backend.url_shortener.model.*;
import com.example.backend.url_shortener.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;


@Service
public class UrlService {
    private final ShortUrlRepository shortUrlRepo;
    private final ClickRepository clickRepo;

    public UrlService(ShortUrlRepository shortUrlRepo, ClickRepository clickRepo) {
        this.shortUrlRepo = shortUrlRepo;
        this.clickRepo = clickRepo;
    }

    public ShortUrl createShortUrl(String originalUrl, String customCode, LocalDateTime expiresAt) {
    String shortCode;
    System.out.println("Custom Code: " + customCode);
    if (customCode != null && !customCode.trim().isEmpty()) {
        Optional<ShortUrl> existing = shortUrlRepo.findByShortCode(customCode);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Custom short code already exists.");
        }
        shortCode = customCode;
    } else {
        shortCode = generateShortCode();
    }

    ShortUrl shortUrl = new ShortUrl();
    shortUrl.setOriginalUrl(originalUrl);
    shortUrl.setShortCode(shortCode);
    shortUrl.setCreatedAt(LocalDateTime.now());
    shortUrl.setExpiresAt(expiresAt); // ✅ save expiry

    return shortUrlRepo.save(shortUrl);
}


    public Optional<ShortUrl> getOriginalUrl(String shortCode) {
    Optional<ShortUrl> optional = shortUrlRepo.findByShortCode(shortCode);
    if (optional.isEmpty()) return Optional.empty();

    ShortUrl url = optional.get();
    if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
        return Optional.empty(); // ✅ expired
    }

    return optional;
}
    public List<UrlDashboard> getUrlsWithClickCount() {
    List<ShortUrl> urls = shortUrlRepo.findAll();
    return urls.stream()
        .map(url -> {
            long clicks = clickRepo.countByShortUrl(url); // ✅ Efficient count
            return new UrlDashboard(
                url.getOriginalUrl(),
                url.getShortCode(),
                clicks,
                url.getCreatedAt(),
                url.getExpiresAt() // ✅ Include expiry
            );
        })
        .collect(Collectors.toList());
}


    public void deleteByShortCode(String shortCode) {
    Optional<ShortUrl> optionalUrl = shortUrlRepo.findByShortCode(shortCode);
    if (optionalUrl.isPresent()) {
        ShortUrl shortUrl = optionalUrl.get();

        // First delete all clicks referencing this short URL
        clickRepo.deleteByShortUrl(shortUrl);

        // Now delete the short URL
        shortUrlRepo.delete(shortUrl);
    }
}

    public void recordClick(ShortUrl shortUrl, String ip) {
        Click click = new Click();
        click.setShortUrl(shortUrl);
        click.setTimestamp(LocalDateTime.now());
        click.setIpAddress(ip);
        clickRepo.save(click);
    }

    public long getClickCount(ShortUrl shortUrl) {
        return clickRepo.findByShortUrl(shortUrl).size();
    }

    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
