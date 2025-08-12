package com.example.backend.url_shortener.controller;

import com.example.backend.url_shortener.dto.ShortenRequest;
import com.example.backend.url_shortener.dto.UrlDashboard;
import com.example.backend.url_shortener.model.Click;
import com.example.backend.url_shortener.model.ShortUrl;
import com.example.backend.url_shortener.repository.ClickRepository;
import com.example.backend.url_shortener.service.UrlService;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/")
public class UrlController {

    private final ClickRepository clickRepository;

    private final UrlService urlService;

    public UrlController(UrlService urlService, ClickRepository clickRepository) {
        this.urlService = urlService;
        this.clickRepository = clickRepository;
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
public ResponseEntity<?> redirect(
        @PathVariable String shortCode,
        HttpServletRequest request) {

    Optional<ShortUrl> result = urlService.getOriginalUrl(shortCode);

    if (result.isEmpty()) {
        // Not found in DB
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("<h1>404 - Link Not Found</h1>");
    }

    ShortUrl shortUrl = result.get();

    // Check if expired
    if (shortUrl.getExpiresAt() != null && shortUrl.getExpiresAt().isBefore(LocalDateTime.now())) {
        return ResponseEntity.status(HttpStatus.GONE)
                .body("<h1>410 - This link has expired</h1>");
    }

    // Log click
    String ip = request.getRemoteAddr();
    String userAgent = request.getHeader("User-Agent");
    urlService.recordClick(shortUrl, ip, userAgent);

    // Redirect to original URL
    return ResponseEntity.status(HttpStatus.FOUND)
            .location(URI.create(shortUrl.getOriginalUrl()))
            .build();
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

    @GetMapping("/api/clicks/{code}")
    public List<Click> getClicks(@PathVariable String code) {
        return urlService.getClicks(code);
    }

    @GetMapping("/api/analytics/top-countries/{shortCode}")
    public List<Map<String, Object>> getTopCountries(@PathVariable String shortCode) {
        return clickRepository.findTopCountries(shortCode);
    }

    @PostMapping("/api/bulk-upload")
    public ResponseEntity<?> bulkUpload(@RequestParam("file") MultipartFile file) {
        List<Map<String, String>> results = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {

            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .build()
                    .parse(reader);

            for (CSVRecord record : records) {
                String originalUrl = record.get("originalUrl");
                String customCode = record.isMapped("customCode") ? record.get("customCode") : null;
                String expiresAtStr = record.isMapped("expiresAt") ? record.get("expiresAt") : null;
                LocalDateTime expiresAt = expiresAtStr != null && !expiresAtStr.isEmpty()
                        ? LocalDateTime.parse(expiresAtStr)
                        : null;

                ShortUrl shortUrl = urlService.createShortUrl(originalUrl, customCode, expiresAt);

                results.add(Map.of(
                        "shortCode", shortUrl.getShortCode(),
                        "originalUrl", shortUrl.getOriginalUrl()
                ));
            }

            return ResponseEntity.ok(results);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("api/urls/search")
    public ResponseEntity<List<ShortUrl>> searchAndFilter(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Integer minClicks,
            @RequestParam(required = false) Integer maxClicks,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<ShortUrl> results = urlService.searchAndFilter(query, minClicks, maxClicks, startDate, endDate);
        return ResponseEntity.ok(results);
    }
        
    
}