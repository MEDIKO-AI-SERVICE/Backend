package com.mediko.mediko_server.domain.translation.domain;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.translation.domain.repository.TranslationType;
import com.mediko.mediko_server.global.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "translation")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Translation extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TranslationType type;

    @Column(name = "text_ko", nullable = false)
    private String textKo;

    @Column(name = "text_en")
    private String textEn;

    @Column(name = "text_vi")
    private String textVi;

    @Column(name = "text_zh_cn")
    private String textZhCn;

    @Column(name = "text_zh_tw")
    private String textZhTw;

    public String getTranslatedText(Language language) {
        return switch (language) {
            case KO -> textKo;
            case EN -> textEn;
            case VI -> textVi;
            case ZH_CN -> textZhCn;
            case ZH_TW -> textZhTw;
            default -> textKo;
        };
    }
}
