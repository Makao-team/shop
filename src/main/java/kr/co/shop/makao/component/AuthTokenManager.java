package kr.co.shop.makao.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import kr.co.shop.makao.config.AuthProperties;
import kr.co.shop.makao.enums.TokenType;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class AuthTokenManager {
    private final AuthProperties authProperties;
    private final Key accessTokenKey;
    private final Key refreshTokenKey;
    private final JwtAlgorithmProvider jwtAlgorithmProvider;

    public AuthTokenManager(AuthProperties authProperties, JwtAlgorithmProvider jwtAlgorithmProvider) {
        this.authProperties = authProperties;
        this.jwtAlgorithmProvider = jwtAlgorithmProvider;
        this.accessTokenKey = Keys.hmacShaKeyFor(authProperties.getAccessTokenSecret().getBytes(StandardCharsets.UTF_8));
        this.refreshTokenKey = Keys.hmacShaKeyFor(authProperties.getRefreshTokenSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String create(String subject, TokenType tokenType) {
        var key = tokenType == TokenType.ACCESS_TOKEN ? accessTokenKey : refreshTokenKey;
        var expiration = tokenType == TokenType.ACCESS_TOKEN ? authProperties.getAccessTokenExpiration() : authProperties.getRefreshTokenExpiration();
        return Jwts.builder()
                .setSubject(subject)
                .signWith(key, jwtAlgorithmProvider.provide())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .compact();
    }

    public String getSubject(String token, TokenType tokenType) {
        var key = tokenType == TokenType.ACCESS_TOKEN ? accessTokenKey : refreshTokenKey;
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
