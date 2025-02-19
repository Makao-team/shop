package kr.co.shop.makao.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public record CommonResponse<T>(String message, T data) {
    public static <T> ResponseEntity<CommonResponse<T>> success(T data) {
        return ResponseEntity.ok(new CommonResponse<>("OK", data));
    }

    public static <T> ResponseEntity<CommonResponse<T>> error(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus).body(new CommonResponse<>(message, null));
    }
}