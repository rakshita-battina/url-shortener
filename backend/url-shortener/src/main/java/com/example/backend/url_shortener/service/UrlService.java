package com.example.backend.url_shortener.service;

import com.example.backend.url_shortener.dto.UrlDashboard;
import com.example.backend.url_shortener.model.*;
import com.example.backend.url_shortener.repository.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jakarta.transaction.Transactional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
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

    // ShortUrl url = optional.get();
    // if (url.getExpiresAt() != null && url.getExpiresAt().isBefore(LocalDateTime.now())) {
    //     return Optional.empty(); // ✅ expired
    // }

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
 @Transactional
    public void softDeleteUrl(String shortCode) {
        shortUrlRepo.softDeleteByShortCode(shortCode);
        clickRepo.deleteByShortUrl(shortUrlRepo.findByShortCode(shortCode).orElseThrow(() -> new IllegalArgumentException("Short URL not found")));
    }

    @Scheduled(cron = "0 0 * * * *") // every hour
    @Transactional
    public void pruneExpiredUrls() {
        shortUrlRepo.pruneExpired();
    }
    public void recordClick(ShortUrl shortUrl, String ipAddress, String userAgent) {

        String browser = parseBrowser(userAgent);
        String os = parseOS(userAgent);

        Click click = new Click();
        click.setShortUrl(shortUrl);
        click.setIpAddress(ipAddress);
        click.setUserAgent(userAgent);
        click.setBrowser(browser);
        click.setOs(os);
        click.setTimestamp(LocalDateTime.now());
        String country = getCountryFromIp(ipAddress);
        click.setCountry(country);
        clickRepo.save(click);
    }
    private String getCountryFromIp(String ip) {
    try {
        // Handle localhost for development
        if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
            return "Localhost";
        }

        String apiUrl = "http://ip-api.com/json/" + ip + "?fields=country";
        HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
        connection.setRequestMethod("GET");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            JsonObject json = JsonParser.parseString(response.toString()).getAsJsonObject();
            return json.get("country").getAsString();
        }
    } catch (Exception e) {
        e.printStackTrace();
        return "Unknown";
    }
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
   

    public List<Click> getClicks(String shortCode) {
        return clickRepo.findByShortUrl_ShortCode(shortCode);
    }
    private String parseBrowser(String ua) {
    if (ua == null) return "Unknown";
    ua = ua.toLowerCase();
    if (ua.contains("chrome")) return "Chrome";
    if (ua.contains("firefox")) return "Firefox";
    if (ua.contains("safari") && !ua.contains("chrome")) return "Safari";
    if (ua.contains("edge")) return "Edge";
    if (ua.contains("opera")) return "Opera";
    return "Other";
}

private String parseOS(String ua) {
    if (ua == null) return "Unknown";
    ua = ua.toLowerCase();
    if (ua.contains("windows")) return "Windows";
    if (ua.contains("mac os")) return "Mac OS";
    if (ua.contains("linux")) return "Linux";
    if (ua.contains("android")) return "Android";
    if (ua.contains("iphone") || ua.contains("ios")) return "iOS";
    return "Other";
}

public List<ShortUrl> searchAndFilter(String query, Integer minClicks, Integer maxClicks, LocalDate startDate, LocalDate endDate) {
    if (query == null || query.trim().isEmpty()) {
        query = null;
    } else {
        query = "%" + query.trim() + "%";
    }
    return shortUrlRepo.searchAndFilter(query, minClicks, maxClicks, startDate, endDate);
}
    
}
