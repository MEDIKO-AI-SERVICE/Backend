package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErPasswordResponseDTO {
    private Integer statusCode;
    private String body;

    public static ErPasswordResponseDTO fromBasicInfo(BasicInfo basicInfo) {
        return new ErPasswordResponseDTO(200, "{\"password\": \"" + basicInfo.getErPassword() + "\"}");
    }
    
    // 사용자 조회용 - 단순 password만 반환
    public static ErPasswordResponseDTO forUser(BasicInfo basicInfo) {
        return new ErPasswordResponseDTO(null, "{\"password\": \"" + basicInfo.getErPassword() + "\"}");
    }
}


