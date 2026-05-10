package org.davision1dyx.catmanguard.web.handler;

import lombok.extern.slf4j.Slf4j;
import org.davision1dyx.catmanguard.base.exception.BizException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public ResponseEntity<Result<Void>> handleBizException(BizException e) {
        log.error("[BizException] code: {}, message: {}, httpCode: {}", e.getCode(), e.getMessage(), e.getHttpCode());

        Result<Void> result = Result.fail(e.getCode(), e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.valueOf(e.getHttpCode()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("[IllegalArgumentException] message: {}", e.getMessage());

        Result<Void> result = Result.fail(400, e.getMessage());
        return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<Void>> handleException(Exception e) {
        log.error("[Exception] message: {}", e.getMessage(), e);

        Result<Void> result = Result.fail(500, "内部错误");
        return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}