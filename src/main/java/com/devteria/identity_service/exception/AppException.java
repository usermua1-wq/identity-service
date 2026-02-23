package com.devteria.identity_service.exception;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AppException extends RuntimeException{
        private ErrorCode errorCode;

    public AppException(ErrorCode errorCode) {
//        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
