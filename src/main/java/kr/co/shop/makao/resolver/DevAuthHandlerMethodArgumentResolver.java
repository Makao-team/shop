package kr.co.shop.makao.resolver;

import kr.co.shop.makao.repository.UserRepository;
import kr.co.shop.makao.response.CommonException;
import kr.co.shop.makao.vo.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Objects;

@RequiredArgsConstructor
public class DevAuthHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final UserRepository userRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Objects.requireNonNull(parameter.getMethod()).getAnnotation(Available.class) != null;
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        var email = webRequest.getHeader("Authorization");
        if (email == null || email.isBlank()) {
            throw CommonException.UNAUTHORIZED.toException("UNAUTHORIZED");
        }
        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> CommonException.UNAUTHORIZED.toException("UNAUTHORIZED"));

        return AuthUser.builder()
                .subject(user.getEmail())
                .role(user.getRole().getValue())
                .build();
    }
}
