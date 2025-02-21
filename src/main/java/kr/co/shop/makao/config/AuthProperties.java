package kr.co.shop.makao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    private Token accessToken;
    private Token refreshToken;

    public String getAccessTokenSecret() {
        return accessToken.secret;
    }

    public String getRefreshTokenSecret() {
        return refreshToken.secret;
    }

    public long getAccessTokenExpiration() {
        return convertToSeconds(accessToken.expiration);
    }

    public long getRefreshTokenExpiration() {
        return convertToSeconds(refreshToken.expiration);
    }

    private Long convertToSeconds(String time) {
        return Arrays
                .stream(time.split("\\*"))
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .reduce(1, (a, b) -> a * b);
    }

    @Setter
    public static class Token {
        private String secret;
        private String expiration;
    }
}
