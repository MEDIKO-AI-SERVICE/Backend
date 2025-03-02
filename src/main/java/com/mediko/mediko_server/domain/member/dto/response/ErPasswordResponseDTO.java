package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ErPasswordResponseDTO {
    private String password;

    public static ErPasswordResponseDTO fromBasicInfo(BasicInfo basicInfo) {
        return new ErPasswordResponseDTO(basicInfo.getErPassword());
    }
}
