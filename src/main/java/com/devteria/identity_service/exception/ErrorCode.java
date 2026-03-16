package com.devteria.identity_service.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(9998,"Invalid message key", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1001,"User existed", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1002,"Username must be at least {min} characters", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1003,"Password must be at least {min} characters", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(1004,"User not found", HttpStatus.NOT_FOUND),
    WRONG_PASSWORD(1005, "Password or Username does not correct", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED(1006, "You don't have permission to access", HttpStatus.FORBIDDEN),
    UNAUTHENTICATED(1007, "You need to login to access", HttpStatus.UNAUTHORIZED),
    INVALID_BIRTHDATE(1008,"Your age must be at least {min}", HttpStatus.BAD_REQUEST)
    ;
    private  int code;
    private String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
        this.code = code;
        this.message = message;
        this.httpStatusCode = httpStatusCode;
    }
}
