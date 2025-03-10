package com.mediko.mediko_server.domain.member.dto.response;

import com.mediko.mediko_server.domain.member.domain.BasicInfo;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class LanguageResponseDTO {
    private Language language;

    public static LanguageResponseDTO fromBasicInfo(BasicInfo basicInfo) {
        return new LanguageResponseDTO(basicInfo.getLanguage());
    }
}
