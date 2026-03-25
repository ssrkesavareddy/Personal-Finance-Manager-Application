package com.example.moneytracker.jwtutil;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private  String SECRET; // min 32 chars
    private final long EXPIRATION = 1000 * 60 * 60; // 1 hour

    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }

    // 🔹 Generate token
    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION))
                .signWith(getSignKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 🔹 Extract email
    public String extractUsername(String token) {
        return extractClaims(token).getSubject();
    }

    // 🔹 Validate token
    public boolean validateToken(String token, String email) {
        String extracted = extractUsername(token);
        return extracted.equals(email) && !isTokenExpired(token);
    }

    // 🔹 Check expiration
    private boolean isTokenExpired(String token) {
        return extractClaims(token).getExpiration().before(new Date());
    }

    // 🔹 Extract all claims
    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
