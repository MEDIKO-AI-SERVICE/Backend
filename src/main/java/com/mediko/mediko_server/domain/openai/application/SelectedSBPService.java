package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedMBPRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedSBPRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SubBodyPartRepository;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedSBPResponseDTO;
import com.mediko.mediko_server.domain.translation.application.TranslationService;
import com.mediko.mediko_server.domain.translation.domain.repository.TranslationType;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;
import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectedSBPService {

    private final SelectedSBPRepository selectedSBPRepository;
    private final SubBodyPartRepository subBodyPartRepository;
    private final SelectedMBPRepository selectedMBPRepository;
    private final TranslationService translationService;

    @Transactional
    public SelectedSBPResponseDTO saveSelectedSBP(
            Member member, SelectedSBPRequestDTO requestDTO, Long selectedMBPId) {

        List<SubBodyPart> allSubBodyParts = subBodyPartRepository.findAll();
        Map<String, String> translationMap = allSubBodyParts.stream()
                .collect(Collectors.toMap(
                        sbp -> translationService.translate(sbp.getDescription(),
                                TranslationType.SUB_BODY_PART,
                                member.getBasicInfo().getLanguage()),
                        SubBodyPart::getDescription
                ));

        List<String> koreanBodyPartNames = requestDTO.getDescription().stream()
                .map(name -> translationMap.getOrDefault(name, name))
                .collect(Collectors.toList());

        List<SubBodyPart> validSubBodyParts = koreanBodyPartNames.stream()
                .map(body -> subBodyPartRepository.findByDescription(body)
                        .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                                String.format("'%s' 부분은 존재하지 않습니다.", body))))
                .collect(Collectors.toList());

        List<Long> sbpIds = validSubBodyParts.stream()
                .map(SubBodyPart::getId)
                .collect(Collectors.toList());

        SelectedMBP selectedMBP = selectedMBPRepository.findById(selectedMBPId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "선택한 주신체 부분이 존재하지 않습니다."));

        SelectedSBP selectedSBP = requestDTO.toEntity()
                .toBuilder()
                .sbpIds(sbpIds)
                .selectedMBP(selectedMBP)
                .member(member)
                .body(koreanBodyPartNames)  // 한국어로 저장
                .build();

        selectedSBPRepository.save(selectedSBP);

        List<String> translatedDescriptions = translationService.translateList(
                koreanBodyPartNames,
                TranslationType.SUB_BODY_PART,
                member.getBasicInfo().getLanguage()
        );

        return SelectedSBPResponseDTO.fromEntity(selectedSBP, translatedDescriptions);
    }

    public SelectedSBPResponseDTO getSelectedSBP(Long selectedSBPId, Member member) {
        SelectedSBP selectedSBP = selectedSBPRepository.findByIdAndMember(selectedSBPId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 세부신체 부분을 찾을 수 없습니다."));

        List<SubBodyPart> subBodyParts = subBodyPartRepository.findAllById(selectedSBP.getSbpIds());
        List<String> descriptions = subBodyParts.stream()
                .map(SubBodyPart::getDescription)
                .collect(Collectors.toList());

        List<String> translatedDescriptions = translationService.translateList(
                descriptions,
                TranslationType.SUB_BODY_PART,
                member.getBasicInfo().getLanguage()
        );

        return SelectedSBPResponseDTO.fromEntity(selectedSBP, translatedDescriptions);
    }

    @Transactional
    public SelectedSBPResponseDTO updateSelectedSBP(
            Long selectedSBPId, Member member, SelectedSBPRequestDTO requestDTO) {

        List<SubBodyPart> allSubBodyParts = subBodyPartRepository.findAll();
        Map<String, String> translationMap = allSubBodyParts.stream()
                .collect(Collectors.toMap(
                        sbp -> translationService.translate(sbp.getDescription(),
                                TranslationType.SUB_BODY_PART,
                                member.getBasicInfo().getLanguage()),
                        SubBodyPart::getDescription
                ));

        List<String> koreanBodyPartNames = requestDTO.getDescription().stream()
                .map(name -> translationMap.getOrDefault(name, name))
                .collect(Collectors.toList());

        List<SubBodyPart> validSubBodyParts = koreanBodyPartNames.stream()
                .map(body -> subBodyPartRepository.findByDescription(body)
                        .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                                String.format("'%s' 부분은 존재하지 않습니다.", body))))
                .collect(Collectors.toList());

        List<Long> sbpIds = validSubBodyParts.stream()
                .map(SubBodyPart::getId)
                .collect(Collectors.toList());

        SelectedSBP selectedSBP = selectedSBPRepository.findByIdAndMember(selectedSBPId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 세부신체 부분을 찾을 수 없습니다."));

        SelectedMBP selectedMBP = selectedSBP.getSelectedMBP();
        selectedSBP.updateSelectedSBP(requestDTO, sbpIds, selectedMBP);
        selectedSBPRepository.save(selectedSBP);

        List<String> translatedDescriptions = translationService.translateList(
                koreanBodyPartNames,
                TranslationType.SUB_BODY_PART,
                member.getBasicInfo().getLanguage()
        );

        return SelectedSBPResponseDTO.fromEntity(selectedSBP, translatedDescriptions);
    }
}
