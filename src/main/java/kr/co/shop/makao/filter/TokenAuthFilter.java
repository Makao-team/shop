package kr.co.shop.makao.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.shop.makao.component.AuthTokenManager;
import kr.co.shop.makao.enums.TokenType;
import kr.co.shop.makao.response.CommonExceptionImpl;
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
            FilterChain filterChain) throws IOException, ServletException {
        verifyToken(request, response);
        filterChain.doFilter(request, response);
    }

    private void verifyToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var token = request.getHeader("Authorization");

        if (token == null || !token.startsWith("Bearer ")) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
            return;
        }

        try {
            authTokenManager.getPayload(token.substring(7), TokenType.ACCESS_TOKEN);
        } catch (CommonExceptionImpl e) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, e.getMessage());
        }
    }
}