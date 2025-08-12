package com.example.backend.url_shortener.repository;

import com.example.backend.url_shortener.model.Click;
import com.example.backend.url_shortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface ClickRepository extends JpaRepository<Click, Long> {
    List<Click> findByShortUrl(ShortUrl shortUrl);
    List<Click> findByShortUrl_ShortCode(String shortCode);
    long countByShortUrl(ShortUrl shortUrl);
    void deleteByShortUrl(ShortUrl shortUrl);
    @Query("SELECT c.country AS country, COUNT(c) AS count " +
       "FROM Click c " +
       "WHERE c.shortUrl.shortCode = :shortCode " +
       "GROUP BY c.country " +
       "ORDER BY count DESC")
List<Map<String, Object>> findTopCountries(@Param("shortCode") String shortCode);

}