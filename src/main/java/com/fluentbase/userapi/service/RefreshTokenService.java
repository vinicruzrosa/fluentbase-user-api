package com.fluentbase.userapi.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";

    private final RedisTemplate<String, String> redisTemplate;
    private final JwtService jwtService;

    public void storeRefreshToken(String refreshToken, UUID userId) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.opsForValue().set(
                key,
                userId.toString(),
                jwtService.getRefreshTokenExpiration(),
                TimeUnit.MILLISECONDS
        );
    }

    public UUID validateRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        String userId = redisTemplate.opsForValue().get(key);
        if (userId == null) {
            return null;
        }
        return UUID.fromString(userId);
    }

    public void deleteRefreshToken(String refreshToken) {
        String key = REFRESH_TOKEN_PREFIX + refreshToken;
        redisTemplate.delete(key);
    }

    public void deleteAllTokensForUser(UUID userId) {
        // In production, consider maintaining a set of tokens per user for efficient cleanup
        // For now, individual token deletion is used on refresh rotation
    }
}
