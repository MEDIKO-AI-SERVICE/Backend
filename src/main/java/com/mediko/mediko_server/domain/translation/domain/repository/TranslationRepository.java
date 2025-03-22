package com.mediko.mediko_server.domain.translation.domain.repository;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.translation.domain.Translation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TranslationRepository extends JpaRepository<Translation, Long> {
    List<Translation> findByTextKoAndType(String textKo, TranslationType type);
    List<Translation> findByTextKoInAndType(List<String> textKos, TranslationType type);

    default Optional<Translation> findByTranslatedTextAndTypeAndLanguage(String translatedText, TranslationType type, Language language) {
        return switch (language) {
            case KO -> findFirstByTextKoAndType(translatedText, type);
            case EN -> findFirstByTextEnAndType(translatedText, type);
            case VI -> findFirstByTextViAndType(translatedText, type);
            case ZH_CN -> findFirstByTextZhCnAndType(translatedText, type);
            case ZH_TW -> findFirstByTextZhTwAndType(translatedText, type);
            default -> findFirstByTextKoAndType(translatedText, type);
        };
    }

    Optional<Translation> findFirstByTextKoAndType(String textKo, TranslationType type);
    Optional<Translation> findFirstByTextEnAndType(String textEn, TranslationType type);
    Optional<Translation> findFirstByTextViAndType(String textVi, TranslationType type);
    Optional<Translation> findFirstByTextZhCnAndType(String textZhCn, TranslationType type);
    Optional<Translation> findFirstByTextZhTwAndType(String textZhTw, TranslationType type);
}
