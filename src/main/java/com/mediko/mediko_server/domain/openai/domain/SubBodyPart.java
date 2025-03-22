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
@Table(name= "sub_body_part")
public class SubBodyPart extends BaseEntity {
    @Column(nullable = false)
    private String body;

    @Column(nullable = false)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mbp_id")
    private MainBodyPart mainBodyPart;

    public String getTranslatedDescription(Language language, TranslationService translationService) {
        return translationService.translate(description, TranslationType.SUB_BODY_PART, language);
    }
}
