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
    ERR_EMAIL_AUTHENTICATION_FAILED,
    ERR_EMAIL_INVALID_RECIPIENT,
    ERR_EMAIL_NETWORK_ISSUE,
    ERR_EMAIL_UNKNOWN_ERROR,
    ;

}
