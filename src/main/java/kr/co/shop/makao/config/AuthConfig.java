package kr.co.shop.makao.config;

import kr.co.shop.makao.filter.TokenAuthFilter;
import kr.co.shop.makao.service.AuthTokenManager;
import kr.co.shop.makao.service.AuthTokenManagerImpl;
import kr.co.shop.makao.service.NoOpAuthTokenManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Slf4j
@Configuration
public class AuthConfig {
    @Bean
    public FilterRegistrationBean<TokenAuthFilter> jwtAuthFilter(AuthTokenManager authTokenManager) {
        FilterRegistrationBean<TokenAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenAuthFilter(authTokenManager));
        registrationBean.addUrlPatterns("/not-working");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public AuthTokenManager authTokenManager(
            @Value("${auth.access-token.secret}") String accessTokenSecret,
            @Value("${auth.refresh-token.secret}") String refreshTokenSecret,
            @Value("${auth.access-token.expiration}") String accessTokenExpiration,
            @Value("${auth.refresh-token.expiration}") String refreshTokenExpiration
    ) {
        if (accessTokenSecret.isBlank() || refreshTokenSecret.isBlank() || convertToSeconds(accessTokenExpiration) <= 0 || convertToSeconds(refreshTokenExpiration) <= 0) {
            log.info("NoOpAuthTokenManager is used");
            return new NoOpAuthTokenManager();
        }
        return new AuthTokenManagerImpl(accessTokenSecret, refreshTokenSecret, convertToSeconds(accessTokenExpiration), convertToSeconds(refreshTokenExpiration));
    }

    private Long convertToSeconds(String time) {
        return Arrays
                .stream(time.split("\\*"))
                .map(String::trim)
                .mapToLong(Long::parseLong)
                .reduce(1, (a, b) -> a * b);
    }

}
