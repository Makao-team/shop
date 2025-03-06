package kr.co.shop.makao.config;

import kr.co.shop.makao.component.JwtAlgorithmProvider;
import kr.co.shop.makao.component.JwtAlgorithmProviderImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {
    @Bean
    public JwtAlgorithmProvider jwtAlgorithmProvider() {
        return new JwtAlgorithmProviderImpl();
    }

    @Bean
    public AuthProperties authProperties(
            @Value("${auth.access-token.secret}") String accessTokenSecret,
            @Value("${auth.access-token.expiration}") String accessTokenExpiration,
            @Value("${auth.refresh-token.secret}") String refreshTokenSecret,
            @Value("${auth.refresh-token.expiration}") String refreshTokenExpiration,
            @Value("${auth.is-dev-env}") String isDevEnv,
            JwtAlgorithmProvider jwtAlgorithmProvider
    ) {
        return new AuthProperties(accessTokenSecret, accessTokenExpiration, refreshTokenSecret, refreshTokenExpiration, isDevEnv, jwtAlgorithmProvider);
    }
}
