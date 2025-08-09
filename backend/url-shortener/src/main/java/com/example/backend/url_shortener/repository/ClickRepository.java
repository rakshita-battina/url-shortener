package com.example.backend.url_shortener.repository;

import com.example.backend.url_shortener.model.Click;
import com.example.backend.url_shortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ClickRepository extends JpaRepository<Click, Long> {
    List<Click> findByShortUrl(ShortUrl shortUrl);
    long countByShortUrl(ShortUrl shortUrl);
    void deleteByShortUrl(ShortUrl shortUrl);
}