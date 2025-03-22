package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DetailedSignResponseDTO {
    private String sign;

    private String description;

    public static DetailedSignResponseDTO fromEntity(
            DetailedSign detailedSign, Language language, TranslationService translationService) {
        return new DetailedSignResponseDTO(
                detailedSign.getSign(),
                detailedSign.getTranslatedDescription(language, translationService)
        );
    }
}
