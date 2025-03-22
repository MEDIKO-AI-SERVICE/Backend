package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.repository.MainBodyPartRepository;
import com.mediko.mediko_server.domain.openai.dto.response.MainBodyPartResponseDTO;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainBodyPartService {
    private final MainBodyPartRepository mainBodyPartRepository;
    private final TranslationService translationService;

    // 주요 신체 전체 조회
    @Transactional(readOnly = true)
    public List<MainBodyPartResponseDTO> findAll(Member member) {
        Language language = member.getBasicInfo().getLanguage();
        List<MainBodyPart> allMainBodyParts = mainBodyPartRepository.findAll();
        return allMainBodyParts.stream()
                .map(mainBodyPart -> MainBodyPartResponseDTO.fromEntity(mainBodyPart, language, translationService))
                .collect(Collectors.toList());
    }
}
