package kr.co.shop.makao.config;

import kr.co.shop.makao.component.JwtAlgorithmProvider;
import kr.co.shop.makao.component.TestJwtAlgorithmProviderImpl;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class TestAuthConfig {
    @Primary
    @Bean
    public JwtAlgorithmProvider testJwtAlgorithmProvider() {
        return new TestJwtAlgorithmProviderImpl();
    }
}
