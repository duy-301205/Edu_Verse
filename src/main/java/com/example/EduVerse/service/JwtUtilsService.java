package com.example.EduVerse.service;

import com.example.EduVerse.entity.User;
import com.example.EduVerse.exception.AppException;
import com.example.EduVerse.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtUtilsService {

    @Value("${spring.jwt.secret}")
    private String secret;

    @Value("${spring.jwt.access-token-expiration}")
    private Long accessExpiration;

    @Value("${spring.jwt.refresh-expiration}")
    private Long refreshExpiration;

    private String buildToken(Map<String,Object> extraClaims, User user, long expirationTime) {
        return Jwts.builder()
                .claims(extraClaims)
                .id(UUID.randomUUID().toString())
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSignInKey())
                .compact();
    }

    public String generateAccessToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        if(user.getRole() != null) {
            claims.put("role", user.getRole().name());
        }

        return buildToken(claims, user, accessExpiration);
    }

    public String generateRefreshToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        return buildToken(claims, user, refreshExpiration);
    }

    private <T> T extractClaims(String token, Function<Claims, T> extracter) {
        final Claims claims = extractAllClaims(token);
        return extracter.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("JWT Token đã hết hạn sử dụng: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        } catch (MalformedJwtException e) {
            log.error("Cấu trúc mã JWT Token không hợp lệ: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_REQUEST);
        } catch (SignatureException e) {
            log.error("Chữ ký JWT Secret Key không trùng khớp hoặc bị giả mạo: {}", e.getMessage());
            throw new AppException(ErrorCode.UNAUTHORIZED);
        } catch (UnsupportedJwtException | IllegalArgumentException e) {
            log.error("Chuỗi JWT rỗng hoặc không được hệ thống hỗ trợ: {}", e.getMessage());
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }
    }

    public SecretKey getSignInKey() {
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
