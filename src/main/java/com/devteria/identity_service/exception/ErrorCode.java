package com.devteria.identity_service.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(9999, "Uncategorized exception"),
    INVALID_KEY(9998,"Invalid message key"),
    USER_EXISTED(1001,"User existed"),
    USERNAME_INVALID(1002,"Username must be at least 3 characters"),
    PASSWORD_INVALID(1003,"Password must be at least 8 characters"),
    USER_NOT_FOUND(1004,"User not found"),
    WRONG_PASSWORD(1005, "Password or Username does not correct")
    ;
    private  int code;
    private String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
