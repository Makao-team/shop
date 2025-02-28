package kr.co.shop.makao.config;

import com.auth0.jwt.algorithms.Algorithm;
import kr.co.shop.makao.component.JwtAlgorithmProvider;
import lombok.Getter;

import java.util.Arrays;

@Getter
public class AuthProperties {
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;
    private final boolean isDevEnv;
    private final Algorithm accessTokenAlgorithm;
    private final Algorithm refreshTokenAlgorithm;

    public AuthProperties(
            String accessTokenSecret,
            String accessTokenExpiration,
            String refreshTokenSecret,
            String refreshTokenExpiration,
            String isDevEnv,
            JwtAlgorithmProvider jwtAlgorithmProvider
    ) {
        this.accessTokenExpiration = convertToSeconds(accessTokenExpiration);
        this.refreshTokenExpiration = convertToSeconds(refreshTokenExpiration);
        this.isDevEnv = Boolean.parseBoolean(isDevEnv);
        this.accessTokenAlgorithm = jwtAlgorithmProvider.provideHmacSha(accessTokenSecret);
        this.refreshTokenAlgorithm = jwtAlgorithmProvider.provideHmacSha(refreshTokenSecret);
    }

    private long convertToSeconds(String time) {
        return Arrays
                .stream(time.split("\\*"))
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .reduce(1, (a, b) -> a * b);
    }
}
