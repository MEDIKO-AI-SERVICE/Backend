package com.mediko.mediko_server.domain.translation.domain.repository;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.translation.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    Optional<Translation> findByTextKoAndType(String textKo, TranslationType type);
    List<Translation> findByTextKoInAndType(List<String> textKos, TranslationType type);



    default Optional<Translation> findByTranslatedTextAndTypeAndLanguage(String translatedText, TranslationType type, Language language) {
        return switch (language) {
            case KO -> findByTextKoAndType(translatedText, type);
            case EN -> findByTextEnAndType(translatedText, type);
            case VI -> findByTextViAndType(translatedText, type);
            case ZH_CN -> findByTextZhCnAndType(translatedText, type);
            case ZH_TW -> findByTextZhTwAndType(translatedText, type);
            default -> findByTextKoAndType(translatedText, type);
        };
    }

    Optional<Translation> findByTextEnAndType(String textEn, TranslationType type);
    Optional<Translation> findByTextViAndType(String textVi, TranslationType type);
    Optional<Translation> findByTextZhCnAndType(String textZhCn, TranslationType type);
    Optional<Translation> findByTextZhTwAndType(String textZhTw, TranslationType type);
}
