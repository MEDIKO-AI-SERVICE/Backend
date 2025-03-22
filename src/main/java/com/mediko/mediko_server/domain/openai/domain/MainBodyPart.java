package com.mediko.mediko_server.domain.openai.domain;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import com.mediko.mediko_server.domain.translation.domain.repository.TranslationType;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name= "main_body_part")
public class MainBodyPart extends BaseEntity {

    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String description;

    public String getTranslatedDescription(Language language, TranslationService translationService) {
        return translationService.translate(description, TranslationType.MAIN_BODY_PART, language);
    }
}
