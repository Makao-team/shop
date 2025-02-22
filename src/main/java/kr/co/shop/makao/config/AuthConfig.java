package kr.co.shop.makao.config;

import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.component.JwtAlgorithmProvider;
import kr.co.shop.makao.component.JwtAlgorithmProviderImpl;
import kr.co.shop.makao.filter.AuthFilter;
import kr.co.shop.makao.filter.NoOpAuthFilter;
import kr.co.shop.makao.filter.TokenAuthFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {
    @Bean
    public FilterRegistrationBean<AuthFilter> jwtAuthFilter(AuthTokenManager authTokenManager, AuthProperties authProperties) {
        AuthFilter authFilter = authProperties.isDevEnv() ? new NoOpAuthFilter() : new TokenAuthFilter(authTokenManager);

        FilterRegistrationBean<AuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authFilter);
        registrationBean.addUrlPatterns("/not-working");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    @Bean
    public JwtAlgorithmProvider jwtAlgorithmProvider() {
        return new JwtAlgorithmProviderImpl();
    }
}
