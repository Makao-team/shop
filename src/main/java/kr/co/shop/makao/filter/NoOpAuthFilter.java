package kr.co.shop.makao.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;

import java.io.IOException;

public class NoOpAuthFilter extends AuthFilter {
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws IOException, ServletException {
        String email = request.getHeader("Authorization");
        if (email.isBlank()) {
            sendErrorResponse(response, HttpStatus.UNAUTHORIZED, "INVALID_TOKEN");
            return;
        }

        request.setAttribute("email", email);
        filterChain.doFilter(request, response);
    }
}
