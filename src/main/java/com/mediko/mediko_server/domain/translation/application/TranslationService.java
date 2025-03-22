package com.mediko.mediko_server.domain.translation.application;

import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.translation.domain.Translation;
import com.mediko.mediko_server.domain.translation.domain.repository.TranslationRepository;
import com.mediko.mediko_server.domain.translation.domain.repository.TranslationType;
import jakarta.persistence.Cacheable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TranslationService {
    private final TranslationRepository translationRepository;

    public String translate(String textKo, TranslationType type, Language language) {
        if (textKo == null) return null;

        List<Translation> translations = translationRepository.findByTextKoAndType(textKo, type);
        if (translations.isEmpty()) {
            return textKo;
        }

        return translations.stream()
                .filter(translation -> translation.getTextKo().equals(textKo))
                .findFirst()
                .map(translation -> translation.getTranslatedText(language))
                .orElse(textKo);
    }

    public List<String> translateList(List<String> textKos, TranslationType type, Language language) {
        if (textKos == null || textKos.isEmpty()) return new ArrayList<>();

        Map<String, Translation> translations = translationRepository
                .findByTextKoInAndType(textKos, type)
                .stream()
                .collect(Collectors.toMap(
                        Translation::getTextKo,
                        translation -> translation,
                        (existing, replacement) -> existing // 중복 시 첫 번째 값 유지
                ));

        return textKos.stream()
                .map(text -> translations
                        .getOrDefault(text, Translation.builder()
                                .textKo(text)
                                .type(type)
                                .build())
                        .getTranslatedText(language))
                .collect(Collectors.toList());
    }

    @Transactional
    public Translation saveTranslation(Translation translation) {
        return translationRepository.save(translation);
    }

    public String getTextKo(String translatedText, TranslationType type, Language language) {
        return translationRepository.findByTranslatedTextAndTypeAndLanguage(translatedText, type, language)
                .map(Translation::getTextKo)
                .orElse(translatedText);
    }
}
