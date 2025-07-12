package com.mediko.mediko_server.global.security;

import java.security.Key;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import com.mediko.mediko_server.domain.member.application.CustomUserDetailsService;
import com.mediko.mediko_server.domain.member.dto.request.TokenRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.TokenResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.UnauthorizedException;
import com.mediko.mediko_server.global.redis.RedisUtil;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import lombok.extern.slf4j.Slf4j;

import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_TOKEN;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenExpirationTime;
    private final long refreshTokenExpirationTime;
    private final CustomUserDetailsService customUserDetailsService;
    private final RedisUtil redisUtil;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.expiration-time}") long accessTokenExpirationTime,
            @Value("${jwt.refresh-expiration-time}") long refreshTokenExpirationTime,
            CustomUserDetailsService customUserDetailsService,
            RedisUtil redisUtil) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationTime = accessTokenExpirationTime;
        this.refreshTokenExpirationTime = refreshTokenExpirationTime;
        this.customUserDetailsService = customUserDetailsService;
        this.redisUtil = redisUtil;
    }


    // Access Token과 Refresh Token 발급 메서드
    public TokenResponseDTO generateToken(Authentication authentication) {
        long now = (new Date()).getTime();
        Date accessTokenExpiresIn = new Date(now + accessTokenExpirationTime);

        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(accessTokenExpiresIn)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setSubject(authentication.getName())
                .setExpiration(new Date(now + refreshTokenExpirationTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        redisUtil.setValues(authentication.getName(), refreshToken, Duration.ofMillis(refreshTokenExpirationTime));

        return TokenResponseDTO.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    // refresh token으로 access token을 재발급하는 메서드
    public TokenResponseDTO reissueToken(TokenRequestDTO tokenRequestDTO) {
        String refreshToken = tokenRequestDTO.getRefreshToken();

        if (!validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException(INVALID_TOKEN, "유효하지 않거나 만료된 refresh token 입니다.");
        }

        String username = getUserNameFromToken(refreshToken);
        redisUtil.deleteValues(username);

        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, "", userDetails.getAuthorities());

        return generateToken(authentication);
    }


    // refresh token 유효성 검사 메서드
    public boolean validateRefreshToken(String token) {
        if (!validateToken(token)) return false;

        try {
            String username = getUserNameFromToken(token);
            String savedToken = (String) redisUtil.getValues(username);
            return token.equals(savedToken);
        } catch (Exception e) {
            log.info("RefreshToken Validation Failed", e);
            return false;
        }
    }


    // JWT Token에서 username을 추출하는 메서드
    public String getUserNameFromToken(String token) {
        return parseClaims(token).getSubject();
    }


    // Access Token에서 인증 정보를 생성하여 반환하는 메서드
    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        String username = claims.getSubject();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);

        if (userDetails == null) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }


    // JWT Token 유효성 검사 메서드
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.info("Invalid JWT Token", e);
        } catch (ExpiredJwtException e) {
            log.info("Expired JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.info("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.info("JWT claims string is empty.", e);
        }
        return false;
    }


    // JWT Token을 파싱하여 Claims를 반환하는 메서드
    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }


    // JWT Token의 만료 시간을 반환하는 메서드
    public Long getExpirationTime(String token) {
        Date expiration = parseClaims(token).getExpiration();
        long now = new Date().getTime();
        return expiration.getTime() - now;
    }
}
