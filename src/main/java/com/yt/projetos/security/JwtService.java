package com.yt.projetos.security;

import com.yt.projetos.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final SecretKey key;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        byte[] keyBytes = secret.length() >= 32
                ? secret.getBytes(java.nio.charset.StandardCharsets.UTF_8)
                : Decoders.BASE64.decode(secret);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);
        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("createdAt", user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public User extractUser(String token) {
        Claims claims = getClaims(token);
        String idStr = claims.getSubject();
        String username = claims.get("username", String.class);
        String createdAtStr = claims.get("createdAt", String.class);
        
        java.time.LocalDateTime createdAt = null;
        if (createdAtStr != null) {
            try {
                createdAt = java.time.LocalDateTime.parse(createdAtStr);
            } catch (Exception ignored) {}
        }
        
        return User.builder()
                .id(UUID.fromString(idStr))
                .username(username)
                .createdAt(createdAt)
                .build();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(getClaims(token).getSubject());
    }

    public boolean isTokenValid(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.getExpiration() == null || claims.getExpiration().after(new Date());
        } catch (Exception ex) {
            return false;
        }
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}