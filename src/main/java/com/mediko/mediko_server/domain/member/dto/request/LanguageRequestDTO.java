package com.mediko.mediko_server.domain.member.dto.request;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LanguageRequestDTO {
    @NotNull(message = "언어 설정은 필수입니다.")
    private Language language;
}
