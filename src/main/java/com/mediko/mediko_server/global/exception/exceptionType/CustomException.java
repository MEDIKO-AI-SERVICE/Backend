package com.mediko.mediko_server.global.exception.exceptionType;

import com.mediko.mediko_server.global.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomException extends RuntimeException{
    ErrorCode errorCode;
    String message;
}
