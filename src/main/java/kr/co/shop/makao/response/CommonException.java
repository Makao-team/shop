package kr.co.shop.makao.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum CommonException {
    BAD_REQUEST(HttpStatus.BAD_REQUEST),
    IMAGE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

    private final HttpStatus status;

    public CommonExceptionImpl toException(String message) {
        return new CommonExceptionImpl(status, message);
    }

    public CommonExceptionImpl toException(String message, Throwable cause) {
        return new CommonExceptionImpl(status, message, cause);
    }
}