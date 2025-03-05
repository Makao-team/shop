package kr.co.shop.makao.config;

import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.resolver.AuthHandlerMethodArgumentResolver;
import kr.co.shop.makao.resolver.DevAuthHandlerMethodArgumentResolver;
import kr.co.shop.makao.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final JwtService jwtService;
    private final AuthProperties authProperties;
    private final UserRepository userRepository;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        var authResolver = authProperties.isDevEnv() ?
                new DevAuthHandlerMethodArgumentResolver(userRepository) :
                new AuthHandlerMethodArgumentResolver(jwtService);

        resolvers.add(authResolver);
    }
}

