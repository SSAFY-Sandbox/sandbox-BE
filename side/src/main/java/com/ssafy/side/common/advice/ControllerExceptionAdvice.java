package com.ssafy.side.common.advice;

import com.ssafy.side.common.exception.FailResponse;
import com.ssafy.side.common.exception.SkillTestingCustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

@RestControllerAdvice
public class ControllerExceptionAdvice {
    @ExceptionHandler(SkillTestingCustomException.class)
    public ResponseEntity<FailResponse> handleGlobalException(SkillTestingCustomException ex) {
        return ResponseEntity.status(ex.getStatus())
                .body(FailResponse.fail(ex.getStatus().value(), ex.getCode().toString()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<FailResponse> handleDefaultException(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(FailResponse.fail(HttpStatus.BAD_REQUEST.value(), ex.getBindingResult().getAllErrors().get(0).getDefaultMessage()));
    }
}
