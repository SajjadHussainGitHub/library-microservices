package com.library_services.gateway_server.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate; // 1 day in seconds
    private static final long REFRESH_EXPIRATION = 1000 * 60 * 60 * 24; // 1 day

    public RefreshTokenService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public void storeRefreshToken(String username, String refreshToken) {
        redisTemplate.opsForValue().set(username, refreshToken, REFRESH_EXPIRATION, TimeUnit.SECONDS);
    }


    public String getRefreshToken(String username) {
        return redisTemplate.opsForValue().get(username);
    }


    public void deleteRefreshToken(String username) {
        redisTemplate.delete(username);
    }
}
