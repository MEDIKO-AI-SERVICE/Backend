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
public class SimpleErPasswordResponseDTO {
    private String password;
    
    public static SimpleErPasswordResponseDTO fromBasicInfo(BasicInfo basicInfo) {
        return new SimpleErPasswordResponseDTO(basicInfo.getErPassword());
    }
} 