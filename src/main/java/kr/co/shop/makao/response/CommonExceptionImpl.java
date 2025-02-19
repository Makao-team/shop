package kr.co.shop.makao.response;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CommonExceptionImpl extends RuntimeException {
    private final HttpStatus status;

    CommonExceptionImpl(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    CommonExceptionImpl(HttpStatus status, String message, Throwable cause) {
        super(message, cause);
        this.status = status;
    }
}