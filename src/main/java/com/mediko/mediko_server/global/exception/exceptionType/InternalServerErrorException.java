package com.mediko.mediko_server.global.exception.exceptionType;

import com.mediko.mediko_server.global.exception.ErrorCode;

public class InternalServerErrorException extends CustomException {
    public InternalServerErrorException(ErrorCode errorCode, String message) {
        super(errorCode, errorCode.getMsg() + " " + message);
    }
}
