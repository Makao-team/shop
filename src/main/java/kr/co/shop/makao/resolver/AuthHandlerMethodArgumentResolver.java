package kr.co.shop.makao.resolver;

import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.filter.Available;
import kr.co.shop.makao.response.CommonException;
import kr.co.shop.makao.vo.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Objects;

@RequiredArgsConstructor
public class AuthHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private final AuthTokenManager authTokenManager;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthUser.class)
                && parameter.hasParameterAnnotation(JwtPayload.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) {
        var token = Objects.requireNonNull(webRequest).getHeader("Authorization");
        var payload = authTokenManager.getPayload(token.substring(7), TokenType.ACCESS_TOKEN);

        var available = Objects.requireNonNull(parameter.getMethod()).getAnnotation(Available.class);
        if (available != null) checkRole(payload.role(), available.roles());

        return AuthUser.builder()
                .subject(payload.subject())
                .role(payload.role())
                .build();
    }

    public void checkRole(String role, UserRole[] roles) {
        Arrays.stream(roles)
                .filter(r -> r.getValue().equals(role))
                .findAny()
                .orElseThrow(() -> CommonException.FORBIDDEN.toException("FORBIDDEN"));
    }
}
