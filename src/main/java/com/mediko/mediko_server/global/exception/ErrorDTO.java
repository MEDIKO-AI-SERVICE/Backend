package com.mediko.mediko_server.global.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseEntity;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDTO {
    private int status;
    private String code;
    private String msg;

    public static ResponseEntity<ErrorDTO> toResponseEntity(ErrorCode e) {
        return ResponseEntity.status(e.getStatus().value())
                .body(ErrorDTO.builder()
                        .status(e.getStatus().value())
                        .code(e.getCode())
                        .msg(e.getMsg())
                        .build());
    }
}
