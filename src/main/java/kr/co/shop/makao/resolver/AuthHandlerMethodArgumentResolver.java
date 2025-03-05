package kr.co.shop.makao.resolver;

import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.enums.UserRole;
import kr.co.shop.makao.response.CommonException;
import kr.co.shop.makao.service.JwtService;
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
    private final JwtService jwtService;

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
        var token = Objects.requireNonNull(webRequest.getHeader("Authorization"));
        var authUser = jwtService.getAuthUser(token.substring(7), TokenType.ACCESS_TOKEN);

        var available = Objects.requireNonNull(parameter.getMethod()).getAnnotation(Available.class);
        if (available != null) checkRole(authUser.role(), available.roles());

        return AuthUser.builder()
                .email(authUser.email())
                .id(authUser.id())
                .role(authUser.role())
                .build();
    }

    public void checkRole(String role, UserRole[] roles) {
        Arrays.stream(roles)
                .filter(r -> r.getValue().equals(role))
                .findAny()
                .orElseThrow(() -> CommonException.FORBIDDEN.toException("FORBIDDEN"));
    }
}
