package org.example.essenceschallenge.app.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
public class AuthService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration.seconds}")
    private String expirationTimeInSeconds;

    public String generateToken(String username) {
        Instant now = Instant.now();
        Instant expiryInstant = now.plusSeconds(Long.parseLong(expirationTimeInSeconds));
        Date nowDate = Date.from(now);
        Date expiryDate = Date.from(expiryInstant);

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(nowDate)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    public boolean validateToken(String token, String username) {
        String tokenUsername = extractUsernameFrom(token);
        return (tokenUsername.equals(username) && !isTokenExpired(token));
    }

    public String extractUsernameFrom(String token) {
        return extractAllClaims(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
}
