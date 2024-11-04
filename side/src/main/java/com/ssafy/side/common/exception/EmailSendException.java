package com.ssafy.side.common.exception;

import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.MessagingException;
import jakarta.mail.SendFailedException;
import org.springframework.http.HttpStatus;

public class EmailSendException extends SkillTestingCustomException {

    public EmailSendException(ErrorMessage code) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, code);
    }

    public static EmailSendException from(Throwable cause) {
        if (cause instanceof AuthenticationFailedException) {
            return new EmailSendException(ErrorMessage.ERR_EMAIL_AUTHENTICATION_FAILED);
        } else if (cause instanceof SendFailedException) {
            return new EmailSendException(ErrorMessage.ERR_EMAIL_INVALID_RECIPIENT);
        } else if (cause instanceof MessagingException) {
            return new EmailSendException(ErrorMessage.ERR_EMAIL_NETWORK_ISSUE);
        } else {
            return new EmailSendException(ErrorMessage.ERR_EMAIL_UNKNOWN_ERROR);
        }
    }
}
