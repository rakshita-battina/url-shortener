package com.example.backend.url_shortener.repository;

import com.example.backend.url_shortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ShortUrlRepository extends JpaRepository<ShortUrl, Long> {
    Optional<ShortUrl> findByShortCode(String shortCode);
    @Query("SELECT s FROM ShortUrl s WHERE s.deleted = false")
List<ShortUrl> findAllActive();

Optional<ShortUrl> findByShortCodeAndDeletedFalse(String shortCode);

@Modifying
@Query("UPDATE ShortUrl s SET s.deleted = true WHERE s.shortCode = :shortCode")
void softDeleteByShortCode(@Param("shortCode") String shortCode);

@Modifying
@Query("DELETE FROM ShortUrl s WHERE s.expiresAt < CURRENT_TIMESTAMP OR s.deleted = true")
void pruneExpired();

 @Query("""
    SELECT u FROM ShortUrl u
    WHERE (:query IS NULL OR u.originalUrl LIKE :query OR u.shortCode LIKE :query)
    AND (:minClicks IS NULL OR u.clickCount >= :minClicks)
    AND (:maxClicks IS NULL OR u.clickCount <= :maxClicks)
    AND (:startDate IS NULL OR u.createdAt >= :startDate)
    AND (:endDate IS NULL OR u.createdAt <= :endDate)
    AND u.deleted = false
""")
List<ShortUrl> searchAndFilter(
        @Param("query") String query,
        @Param("minClicks") Integer minClicks,
        @Param("maxClicks") Integer maxClicks,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
);
}
