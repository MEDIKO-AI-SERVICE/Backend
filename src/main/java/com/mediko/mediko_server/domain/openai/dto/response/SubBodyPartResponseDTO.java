package com.mediko.mediko_server.domain.openai.dto.response;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubBodyPartResponseDTO {

    private String body;

    private String description;

    private Long mainBodyPartId;

    public static SubBodyPartResponseDTO fromEntity(SubBodyPart subBodyPart, Language language, TranslationService translationService) {
        return new SubBodyPartResponseDTO(
                subBodyPart.getBody(),
                subBodyPart.getTranslatedDescription(language, translationService),
                subBodyPart.getMainBodyPart().getId()
        );
    }
}
