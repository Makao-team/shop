package kr.co.shop.makao.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@Slf4j
public class GlobalControllerAdvice {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonResponse<String>> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return CommonResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러가 발생하였습니다. 관리자에게 문의주세요.");
    }

    @ExceptionHandler(CommonExceptionImpl.class)
    public ResponseEntity<CommonResponse<String>> handleCommonExceptionImpl(CommonExceptionImpl e) {
        log.info(e.getMessage(), e);
        return CommonResponse.error(e.getStatus(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonResponse<String>> handleValidationException(MethodArgumentNotValidException e) {
        log.info(e.getMessage(), e);
        return CommonResponse.error(HttpStatus.BAD_REQUEST, e.getBindingResult().getAllErrors().get(0).getDefaultMessage());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<CommonResponse<String>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.info(e.getMessage(), e);
        return CommonResponse.error(HttpStatus.BAD_REQUEST, e.getCause().getCause().getMessage());
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<CommonResponse<String>> handleNoResourceFoundException(NoResourceFoundException e) {
        log.info(e.getMessage(), e);
        return CommonResponse.error(HttpStatus.NOT_FOUND, "NOT_FOUND");
    }
}

