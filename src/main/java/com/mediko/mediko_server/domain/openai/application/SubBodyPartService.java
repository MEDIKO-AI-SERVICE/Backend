package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.domain.repository.MainBodyPartRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SubBodyPartRepository;
import com.mediko.mediko_server.domain.openai.dto.response.SubBodyPartResponseDTO;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import com.mediko.mediko_server.domain.translation.domain.repository.TranslationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubBodyPartService {
    private final SubBodyPartRepository subBodyPartRepository;
    private final TranslationService translationService;
    private final MainBodyPartRepository mainBodyPartRepository;

    // 세부 신체 전체 조회
    public List<SubBodyPartResponseDTO> findAll(Member member) {
        Language language = member.getBasicInfo().getLanguage();
        List<SubBodyPart> allSubBodyParts = subBodyPartRepository.findAll();
        return allSubBodyParts.stream()
                .map(subBodyPart -> SubBodyPartResponseDTO.fromEntity(subBodyPart, language, translationService))
                .collect(Collectors.toList());
    }


    // 세부 신체 부분 조회
    public Map<String, List<SubBodyPartResponseDTO>> getSubBodyPartsByMainBodyPartBodies(
            List<String> translatedBodies, Member member) {
        Language language = member.getBasicInfo().getLanguage();

        List<String> koreanBodies = translatedBodies.stream()
                .map(translatedBody -> translationService.getTextKo(translatedBody, TranslationType.MAIN_BODY_PART, language))
                .collect(Collectors.toList());

        List<MainBodyPart> mainBodyParts = mainBodyPartRepository.findByDescriptionIn(koreanBodies);

        Map<String, List<SubBodyPartResponseDTO>> groupedResult = new LinkedHashMap<>();

        for (MainBodyPart mainBodyPart : mainBodyParts) {
            List<SubBodyPart> relatedSubBodyParts = subBodyPartRepository
                    .findByMainBodyPartId(mainBodyPart.getId());

            String translatedMainBodyPart = translationService.translate(
                    mainBodyPart.getDescription(),
                    TranslationType.MAIN_BODY_PART,
                    language
            );

            List<SubBodyPartResponseDTO> dtos = relatedSubBodyParts.stream()
                    .map(subBodyPart -> SubBodyPartResponseDTO.fromEntity(subBodyPart, language, translationService))
                    .collect(Collectors.toList());

            groupedResult.put(translatedMainBodyPart, dtos);

            log.info("Translated Main Body Part: {}", translatedMainBodyPart);
            log.info("Sub Body Parts for {}: {}", translatedMainBodyPart, dtos);
        }

        log.info("Grouped Result: {}", groupedResult);

        return groupedResult;
    }
}
