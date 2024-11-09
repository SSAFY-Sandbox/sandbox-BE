package com.ssafy.side.common.exception;

import org.springframework.http.HttpStatus;

public class InternalServerException extends SkillTestingCustomException {

    public InternalServerException(ErrorMessage code) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, code);
    }
}
