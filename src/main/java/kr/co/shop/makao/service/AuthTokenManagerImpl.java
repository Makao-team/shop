package kr.co.shop.makao.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@RequiredArgsConstructor
public class AuthTokenManagerImpl implements AuthTokenManager {
    private final String accessTokenSecret;
    private final String refreshTokenSecret;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    @Override
    public String createAccessToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .signWith(Keys.hmacShaKeyFor(accessTokenSecret.getBytes()))
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .compact();
    }

    @Override
    public String createRefreshToken(String subject) {
        return Jwts.builder()
                .setSubject(subject)
                .signWith(Keys.hmacShaKeyFor(refreshTokenSecret.getBytes()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpiration))
                .compact();
    }

    @Override
    public String getSubjectFromAccessToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(accessTokenSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    @Override
    public String getSubjectFromRefreshToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(refreshTokenSecret.getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
