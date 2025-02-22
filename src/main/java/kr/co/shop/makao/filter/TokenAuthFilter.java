package kr.co.shop.makao.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.enums.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class TokenAuthFilter extends AuthFilter {
    private final AuthTokenManager authTokenManager;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException {
        String token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
            return;
        }

        token = token.substring(7);

        try {
            String subject = authTokenManager.getSubject(token, TokenType.ACCESS_TOKEN);

            if (subject.isEmpty()) {
                sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
                return;
            }

            request.setAttribute("email", subject);
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.info("Invalid token", e);
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN");
        }
    }
}
