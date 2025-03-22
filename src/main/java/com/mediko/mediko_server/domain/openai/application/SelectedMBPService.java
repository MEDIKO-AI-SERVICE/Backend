package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import com.mediko.mediko_server.domain.openai.domain.repository.MainBodyPartRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedMBPRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedSBPRepository;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedMBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedMBPResponseDTO;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import com.mediko.mediko_server.domain.translation.domain.repository.TranslationType;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;
import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectedMBPService {

    private final SelectedMBPRepository selectedMBPRepository;
    private final SelectedSBPRepository selectedSBPRepository;
    private final MainBodyPartRepository mainBodyPartRepository;
    private final TranslationService translationService;

    // 선택한 주요 신체 저장
    @Transactional
    public SelectedMBPResponseDTO saveSelectedMBP(SelectedMBPRequestDTO requestDTO, Member member) {
        List<String> translatedBodyPartNames = requestDTO.getDescription();

        if (translatedBodyPartNames == null || translatedBodyPartNames.isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 선택해야 합니다.");
        }
        if (translatedBodyPartNames.size() > 2) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 2개 이내로 선택하세요.");
        }

        List<MainBodyPart> allMainBodyParts = mainBodyPartRepository.findAll();
        Map<String, String> translationMap = allMainBodyParts.stream()
                .collect(Collectors.toMap(
                        mbp -> translationService.translate(mbp.getDescription(), TranslationType.MAIN_BODY_PART, member.getBasicInfo().getLanguage()),
                        MainBodyPart::getDescription
                ));

        List<String> koreanBodyPartNames = translatedBodyPartNames.stream()
                .map(name -> translationMap.getOrDefault(name, name))
                .collect(Collectors.toList());

        List<MainBodyPart> foundMainBodyParts = mainBodyPartRepository.findByDescriptionIn(koreanBodyPartNames);

        if (foundMainBodyParts.size() != koreanBodyPartNames.size()) {
            throw new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 주신체 부분이 포함되어 있습니다.");
        }

        List<Long> mbpIds = foundMainBodyParts.stream()
                .map(MainBodyPart::getId)
                .collect(Collectors.toList());

        SelectedMBP selectedMBP = requestDTO.toEntity()
                .toBuilder()
                .member(member)
                .mbpIds(mbpIds)
                .body(koreanBodyPartNames)
                .build();

        selectedMBP = selectedMBPRepository.save(selectedMBP);

        List<MainBodyPart> mainBodyParts = mainBodyPartRepository.findAllById(selectedMBP.getMbpIds());
        List<String> descriptions = mainBodyParts.stream()
                .map(MainBodyPart::getDescription)
                .collect(Collectors.toList());

        List<String> translatedDescriptions = translationService.translateList(
                descriptions,
                TranslationType.MAIN_BODY_PART,
                member.getBasicInfo().getLanguage()
        );

        return SelectedMBPResponseDTO.fromEntity(selectedMBP, translatedDescriptions);
    }


    //선택한 주요 신체 조회
    public SelectedMBPResponseDTO getSelectedMBP(Long selectedMbpId, Member member) {
        SelectedMBP selectedMBP = selectedMBPRepository.findByIdAndMember(selectedMbpId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "선택된 신체 부분이 없습니다."));

        List<MainBodyPart> mainBodyParts = mainBodyPartRepository.findAllById(selectedMBP.getMbpIds());
        List<String> descriptions = mainBodyParts.stream()
                .map(MainBodyPart::getDescription)
                .collect(Collectors.toList());

        List<String> translatedDescriptions = translationService.translateList(
                descriptions,
                TranslationType.MAIN_BODY_PART,
                member.getBasicInfo().getLanguage()
        );

        return SelectedMBPResponseDTO.fromEntity(selectedMBP, translatedDescriptions);
    }


    //선택한 주요 신체 수정
    @Transactional
    public SelectedMBPResponseDTO updateSelectedMBP(Long selectedMbpId, SelectedMBPRequestDTO requestDTO, Member member) {
        SelectedMBP selectedMBP = selectedMBPRepository.findById(selectedMbpId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "선택된 주신체 부분이 존재하지 않습니다."));

        List<MainBodyPart> allMainBodyParts = mainBodyPartRepository.findAll();
        Map<String, String> translationMap = allMainBodyParts.stream()
                .collect(Collectors.toMap(
                        mbp -> translationService.translate(mbp.getDescription(), TranslationType.MAIN_BODY_PART, member.getBasicInfo().getLanguage()),
                        MainBodyPart::getDescription
                ));

        List<String> koreanBodyPartNames = requestDTO.getDescription().stream()
                .map(name -> translationMap.getOrDefault(name, name))
                .collect(Collectors.toList());

        List<MainBodyPart> mainBodyParts = mainBodyPartRepository.findByDescriptionIn(koreanBodyPartNames);
        if (mainBodyParts.size() != koreanBodyPartNames.size()) {
            throw new BadRequestException(INVALID_PARAMETER, "존재하지 않는 신체부분이 포함되어 있습니다.");
        }

        List<SelectedSBP> selectedSBPs = selectedSBPRepository.findBySelectedMBPAndMember(selectedMBP, member);
        if (!selectedSBPs.isEmpty()) {
            selectedSBPRepository.deleteAll(selectedSBPs);
        }

        List<Long> mbpIds = mainBodyParts.stream()
                .map(MainBodyPart::getId)
                .collect(Collectors.toList());

        selectedMBP.updateSelectedMBP(requestDTO, mbpIds);
        selectedMBPRepository.save(selectedMBP);

        List<String> descriptions = mainBodyParts.stream()
                .map(MainBodyPart::getDescription)
                .collect(Collectors.toList());

        List<String> translatedDescriptions = translationService.translateList(
                descriptions,
                TranslationType.MAIN_BODY_PART,
                member.getBasicInfo().getLanguage()
        );

        return SelectedMBPResponseDTO.fromEntity(selectedMBP, translatedDescriptions);
    }
}
