package com.mediko.mediko_server.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 400 Bad Request
    DATA_NOT_EXIST(HttpStatus.BAD_REQUEST, "4000_DATA_NOT_EXIST", "데이터가 존재하지 않습니다."),
    DATA_ALREADY_EXIST(HttpStatus.BAD_REQUEST, "4001_DATA_ALREADY_EXIST", "데이터가 이미 존재합니다."),
    DATA_ALREADY_DELETED(HttpStatus.BAD_REQUEST, "4002_DATA_ALREADY_DELETED", "데이터가 이미 삭제되었습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "4003_INVALID_PARAMETER", "유효하지 않은 파라미터입니다."),
    MISSING_REQUIRED_FIELD(HttpStatus.BAD_REQUEST, "4004_MISSING_REQUIRED_FIELD", "필수 입력 항목이 누락되었습니다."),
    INVALID_FORMAT(HttpStatus.BAD_REQUEST, "4005_INVALID_FORMAT", "입력 형식이 올바르지 않습니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.BAD_REQUEST, "4006_UNAUTHORIZED_ACCESS", "허용되지 않은 접근입니다."),

    // 401 Unauthorized
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "4010_UNAUTHORIZED", "로그인이 필요합니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "4011_TOKEN_EXPIRED", "토큰이 만료되었습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "4012_INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "4013_ACCESS_DENIED", "접근 권한이 없습니다."),
    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "4014_INVALID_CREDENTIALS", "잘못된 자격 증명입니다."),

    // 500 Internal Server Error
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "5000_INTERNAL_SERVER_ERROR", "서버 오류가 발생했습니다."),
    FILE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "5001_FILE_UPLOAD_FAIL", "파일 업로드에 실패했습니다."),
    FILE_DELETE_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "5002_FILE_DELETE_FAIL", "파일 삭제에 실패했습니다."),
    FAILED_TO_SEND_ALERT(HttpStatus.INTERNAL_SERVER_ERROR, "5003_FAILED_TO_SEND_ALERT", "알림 전송에 실패하였습니다."),

    // 503 Service Unavailable
    SERVICE_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "5030_SERVICE_UNAVAILABLE", "현재 서비스 이용이 불가능합니다. 잠시 후 다시 시도해주세요."),
    SERVICE_TIMEOUT(HttpStatus.SERVICE_UNAVAILABLE, "5031_SERVICE_TIMEOUT", "서비스 응답 시간이 초과되었습니다."),
    DATA_UNAVAILABLE(HttpStatus.SERVICE_UNAVAILABLE, "5032_DATA_UNAVAILABLE", "데이터를 불러올 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String msg;
}
