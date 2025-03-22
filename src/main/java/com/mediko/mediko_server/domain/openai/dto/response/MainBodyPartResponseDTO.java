package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MainBodyPartResponseDTO {

    private String body;

    private String description;

    public static MainBodyPartResponseDTO fromEntity(MainBodyPart mainBodyPart, Language language, TranslationService translationService) {
        return new MainBodyPartResponseDTO(
                mainBodyPart.getBody(),
                mainBodyPart.getTranslatedDescription(language, translationService)  // 번역된 description 사용
        );
    }
}
