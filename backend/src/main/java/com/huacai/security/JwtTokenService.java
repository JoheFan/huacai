package com.huacai.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import javax.crypto.SecretKey;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenService {

    private final JwtProperties jwtProperties;
    private final ConcurrentHashMap<String, Long> tokenBlacklist = new ConcurrentHashMap<>();

    public JwtTokenService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    public String generateToken(AuthUser authUser) {
        Instant now = Instant.now();
        Instant expiredAt = now.plusSeconds(jwtProperties.getExpireSeconds());
        return Jwts.builder()
                .subject(String.valueOf(authUser.getUserId()))
                .claim("username", authUser.getUsername())
                .claim("realName", authUser.getRealName())
                .claim("orgId", authUser.getOrgId())
                .claim("orgName", authUser.getOrgName())
                .claim("roles", authUser.getRoles())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiredAt))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public long getExpireSeconds() {
        return jwtProperties.getExpireSeconds();
    }

    /**
     * 将token加入黑名单
     */
    public void invalidateToken(String token) {
        try {
            Claims claims = parseToken(token);
            Date expiration = claims.getExpiration();
            if (expiration != null) {
                tokenBlacklist.put(token, expiration.getTime());
                cleanupExpiredTokens();
            }
        } catch (Exception e) {
            // token解析失败，忽略
        }
    }

    /**
     * 检查token是否已被加入黑名单
     */
    public boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }

    private void cleanupExpiredTokens() {
        long now = System.currentTimeMillis();
        tokenBlacklist.entrySet().removeIf(entry -> entry.getValue() < now);
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(JwtSecretSupport.decodeAndValidate(jwtProperties.getSecret()));
    }
}
