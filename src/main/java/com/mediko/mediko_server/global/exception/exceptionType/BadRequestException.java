package com.mediko.mediko_server.global.exception.exceptionType;

import com.mediko.mediko_server.global.exception.ErrorCode;

public class BadRequestException extends CustomException {
    public BadRequestException(ErrorCode errorCode, String message) {
        super(errorCode, errorCode.getMsg() + " " + message);
    }
}
