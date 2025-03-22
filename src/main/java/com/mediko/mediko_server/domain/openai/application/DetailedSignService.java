package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import com.mediko.mediko_server.domain.openai.domain.repository.DetailedSignRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SubBodyPartRepository;
import com.mediko.mediko_server.domain.openai.dto.response.DetailedSignResponseDTO;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import com.mediko.mediko_server.domain.translation.domain.repository.TranslationType;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetailedSignService {
    private final DetailedSignRepository detailedSignRepository;
    private final SubBodyPartRepository subBodyPartRepository;
    private final TranslationService translationService;

    public List<DetailedSignResponseDTO> findAll(Member member) {
        Language language = member.getBasicInfo().getLanguage();
        List<DetailedSign> allDetailedSigns = detailedSignRepository.findAll();

        return allDetailedSigns.stream()
                .map(detailedSign -> DetailedSignResponseDTO.fromEntity(detailedSign, language, translationService))
                .collect(Collectors.toList());
    }

    public List<DetailedSignResponseDTO> getDetailedSignsByBodyPart(String description, Member member) {
        Language language = member.getBasicInfo().getLanguage();

        // 번역된 body값을 한국어로 변환
        String koreanDescription = translationService.getTextKo(description, TranslationType.SUB_BODY_PART, language);

        subBodyPartRepository.findByDescription(koreanDescription)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당하는 신체 부위를 찾을 수 없습니다."));

        List<DetailedSign> detailedSigns = detailedSignRepository.findBySubBodyPartDescription(koreanDescription);

        return detailedSigns.stream()
                .map(detailedSign -> DetailedSignResponseDTO.fromEntity(detailedSign, language, translationService))
                .collect(Collectors.toList());
    }
}
