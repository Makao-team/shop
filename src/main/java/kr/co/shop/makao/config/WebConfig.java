package kr.co.shop.makao.config;

import kr.co.shop.makao.component.JwtManager;
import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.resolver.AuthHandlerMethodArgumentResolver;
import kr.co.shop.makao.resolver.DevAuthHandlerMethodArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtManager jwtManager;
    private final AuthProperties authProperties;
    private final UserRepository userRepository;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        var authResolver = authProperties.isDevEnv() ?
                new DevAuthHandlerMethodArgumentResolver(userRepository) :
                new AuthHandlerMethodArgumentResolver(jwtManager);

        resolvers.add(authResolver);
    }
}

