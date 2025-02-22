package kr.co.shop.makao.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.shop.makao.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public abstract class CommonOncePerRequestFilter extends OncePerRequestFilter {
    protected final ObjectMapper objectMapper = new ObjectMapper();

    protected void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        CommonResponse<Object> errorResponse = new CommonResponse<>(message, null);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}
