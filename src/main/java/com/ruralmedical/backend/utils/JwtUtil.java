package com.ruralmedical.backend.utils;

import com.ruralmedical.backend.config.filter.JwtRequestFilter;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.*;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    private static final long EXPIRATION_TIME = 60 * 60 * 1000L; // 1小时
    private static final byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("JWT验证失败: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        try {
            Claims claims = extractClaims(token);
            return claims != null ? claims.getSubject() : null;
        } catch (JwtException | IllegalArgumentException e) {
            logger.warn("从JWT提取用户名失败: {}", e.getMessage());
            return null;
        }
    }

    private Boolean isTokenExpired(String token) {
            Claims claims = extractClaims(token);
            return claims != null && claims.getExpiration().before(new Date());
    }

    private Claims extractClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            logger.warn("JWT已过期: {}", e.getMessage());
            return e.getClaims(); // 即使过期也返回claims，以便获取信息
        } catch (UnsupportedJwtException | MalformedJwtException e) {
            logger.warn("无效的JWT: {}", e.getMessage());
            return null;
        } catch (IllegalArgumentException e) {
            logger.warn("JWT参数非法: {}", e.getMessage());
            return null;
        } catch (JwtException e) {
            logger.warn("JWT处理失败: {}", e.getMessage());
            return null;
        }
    }
}
