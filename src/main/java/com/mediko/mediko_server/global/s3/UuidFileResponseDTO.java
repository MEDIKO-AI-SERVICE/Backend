package com.mediko.mediko_server.global.s3;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UuidFileResponseDTO {
    private Long id;
    private String fileUrl;

    public static UuidFileResponseDTO from(UuidFile uuidFile) {
        return new UuidFileResponseDTO(
                uuidFile.getId(),
                uuidFile.getFileUrl()
        );
    }
}
