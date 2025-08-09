// package com.example.backend.url_shortener.service;

// import java.nio.charset.StandardCharsets;
// import java.security.Key;
// import java.util.Date;


// import org.springframework.stereotype.Service;

// import io.jsonwebtoken.Claims;
// import io.jsonwebtoken.Jwts;
// import io.jsonwebtoken.SignatureAlgorithm;
// import io.jsonwebtoken.security.Keys;

// @Service
//     public class JwtService {
//         private static final String SECRET_KEY = "p@ssw0rd_ThisIsSuperSecretAndLongEnough123!"; // Must be 32+ chars

//     private Key getSigningKey() {
//         return Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
//     }


//     public String generateToken(String username, String role) {
//     return Jwts.builder()
//         .setSubject(username)
//         .claim("role", role)
//         .setIssuedAt(new Date())
//         .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
//         .signWith(getSigningKey(), SignatureAlgorithm.HS256)
//         .compact();
// }


//     public Claims extractClaims(String token) {
//         return Jwts.parserBuilder()
//             .setSigningKey(Keys.hmacShaKeyFor(SECRET_KEY.getBytes()))
//             .build()
//             .parseClaimsJws(token)
//             .getBody();
//     }

//     public String extractUsername(String token) {
//         return extractClaims(token).getSubject();
//     }

//     public String extractRole(String token) {
//         return (String) extractClaims(token).get("role");
//     }
// }
