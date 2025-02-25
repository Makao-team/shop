package kr.co.shop.makao.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Setter
@Component
@ConfigurationProperties(prefix = "auth")
public class AuthProperties {
    @Getter
    private Token accessToken;
    @Getter
    private Token refreshToken;
    private String isDevEnv;

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

    public boolean isDevEnv() {
        return Boolean.parseBoolean(isDevEnv);
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
