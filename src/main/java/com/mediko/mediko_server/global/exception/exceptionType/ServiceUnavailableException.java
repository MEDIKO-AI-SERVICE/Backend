package com.mediko.mediko_server.global.exception.exceptionType;

import com.mediko.mediko_server.global.exception.ErrorCode;

public class ServiceUnavailableException extends CustomException {
    public ServiceUnavailableException(ErrorCode errorCode, String message) {
        super(errorCode, errorCode.getMsg() + " " + message);
    }
}
