package com.mediko.mediko_server.global.exception.exceptionType;

import com.mediko.mediko_server.global.exception.ErrorCode;

public class UnauthorizedException extends CustomException {
    public UnauthorizedException(ErrorCode errorCode, String message) {
        super(errorCode, errorCode.getMsg() + " " + message);
    }
}
