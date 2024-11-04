package com.ssafy.side.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    /**
     * 400 Bad Request
     */
    ERR_MISSING_AUTHORIZATION_CODE,
    ERR_INVALID_EMAIL_INFO,
    /**
     * 401 UNAUTHORIZED
     */
    ERR_UNAUTORIZED,
    ERR_REFRESH_TOKEN_EXPIRED,
    ERR_NO_COOKIE,
    ERR_NO_REFRESH_TOKEN_IN_COOKIE,
    ERR_NO_ACCESS_TOKEN_IN_COOKIE,
    ERR_ACCESS_TOKEN_EXPIRED,

    /**
     * 404 NOT_FOUND
     */
    ERR_NOT_FOUND_MEMBER,

    /**
     * 500 Internal Server Error - Email Errors
     */
    ERR_EMAIL_FAILED_TO_SEND,
    ;

}
