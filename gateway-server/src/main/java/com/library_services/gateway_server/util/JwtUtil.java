package com.library_services.gateway_server.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET_KEY = "my-super-secret-key-that-is-very-strong-12345"; // >= 32 chars
    private static final String REFRESH_SECRET_KEY = "my-super-secret-key-that-is-very-strong-12345";
    private static final long ACCESS_EXPIRATION = 1000 * 60 * 15; // 15 minutes
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    private Key getSigningAccessKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    private Key getSigningRefreshKey(){
        return Keys.hmacShaKeyFor(REFRESH_SECRET_KEY.getBytes());
    }


    public String generateAccessToken(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION))
                .signWith(this.getSigningAccessKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration( new Date(System.currentTimeMillis() + REFRESH_EXPIRATION))
                .signWith(this.getSigningRefreshKey(), SignatureAlgorithm.HS256)
                .compact();
    }



    private Key getSigningKey(boolean isRefreshToken) {
        String secret = isRefreshToken ? REFRESH_SECRET_KEY : SECRET_KEY;
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(String token, boolean isRefreshToken) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey(isRefreshToken))
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }



    public boolean validateToken(String token, boolean isRefreshToken) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey(isRefreshToken))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
