package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import com.mediko.mediko_server.domain.openai.domain.SelectedSign;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import com.mediko.mediko_server.domain.openai.domain.repository.DetailedSignRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedSignRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedSBPRepository;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSignRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedSignResponseDTO;
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
public class SelectedSignService {

    private final SelectedSignRepository selectedSignRepository;
    private final DetailedSignRepository detailedSignRepository;
    private final SelectedSBPRepository selectedSBPRepository;
    private final TranslationService translationService;

    private List<DetailedSign> getValidDetailedSigns(SelectedSBP selectedSBP, List<String> descriptions) {
        Set<String> requestDescriptions = new HashSet<>(descriptions);
        return detailedSignRepository
                .findBySubBodyPartIdIn(selectedSBP.getSbpIds())
                .stream()
                .filter(sign -> requestDescriptions.contains(sign.getDescription()))
                .collect(Collectors.toList());
    }

    @Transactional
    public SelectedSignResponseDTO saveSelectedSign(
            Member member, SelectedSignRequestDTO requestDTO, Long selectedSBPId) {

        SelectedSBP selectedSBP = selectedSBPRepository.findById(selectedSBPId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "선택한 세부 신체 부분이 존재하지 않습니다."));

        List<DetailedSign> validDetailedSigns = getValidDetailedSigns(selectedSBP, requestDTO.getDescription());

        Map<String, String> translationMap = validDetailedSigns.stream()
                .collect(Collectors.toMap(
                        sign -> translationService.translate(sign.getDescription(),
                                TranslationType.DETAILED_SIGN,
                                member.getBasicInfo().getLanguage()),
                        DetailedSign::getDescription,
                        (existing, replacement) -> existing
                ));

        List<String> koreanSigns = requestDTO.getDescription().stream()
                .map(desc -> translationMap.getOrDefault(desc, desc))
                .collect(Collectors.toList());

        List<Long> selectedSignIds = new ArrayList<>();
        for (String description : koreanSigns) {
            DetailedSign detailedSign = validDetailedSigns.stream()
                    .filter(sign -> sign.getDescription().equals(description))
                    .findFirst()
                    .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                            "선택한 세부 신체 부분에 해당하지 않는 증상이 포함되어 있습니다."));
            selectedSignIds.add(detailedSign.getId());
        }

        SelectedSign selectedSign = requestDTO.toEntity()
                .toBuilder()
                .signIds(selectedSignIds)
                .sign(koreanSigns)
                .selectedSBP(selectedSBP)
                .member(member)
                .build();

        selectedSignRepository.save(selectedSign);

        List<String> translatedSigns = translationService.translateList(
                koreanSigns,
                TranslationType.DETAILED_SIGN,
                member.getBasicInfo().getLanguage()
        );

        return SelectedSignResponseDTO.fromEntity(selectedSign, translatedSigns);
    }

    public SelectedSignResponseDTO getSelectedSign(
            Long selectedDetailedSignId, Member member) {
        SelectedSign selectedSign = selectedSignRepository
                .findByIdAndMember(selectedDetailedSignId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 상세 증상을 찾을 수 없습니다."));

        List<String> translatedSigns = translationService.translateList(
                selectedSign.getSign(),
                TranslationType.DETAILED_SIGN,
                member.getBasicInfo().getLanguage()
        );

        return SelectedSignResponseDTO.fromEntity(selectedSign, translatedSigns);
    }

    @Transactional
    public SelectedSignResponseDTO updateSelectedSign(
            SelectedSignRequestDTO requestDTO, Long selectedSignId, Member member) {
        SelectedSign selectedSign = selectedSignRepository
                .findByIdAndMember(selectedSignId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 상세 증상을 찾을 수 없습니다."));

        SelectedSBP selectedSBP = selectedSign.getSelectedSBP();

        List<Long> newSignIds = new ArrayList<>();
        List<String> koreanSigns = new ArrayList<>();

        if (requestDTO.getDescription() != null && !requestDTO.getDescription().isEmpty()) {
            List<DetailedSign> validDetailedSigns = getValidDetailedSigns(selectedSBP, requestDTO.getDescription());

            Map<String, String> translationMap = validDetailedSigns.stream()
                    .collect(Collectors.toMap(
                            sign -> translationService.translate(sign.getDescription(),
                                    TranslationType.DETAILED_SIGN,
                                    member.getBasicInfo().getLanguage()),
                            DetailedSign::getDescription,
                            (existing, replacement) -> existing
                    ));

            for (String description : requestDTO.getDescription()) {
                String koreanDescription = translationMap.getOrDefault(description, description);
                DetailedSign detailedSign = validDetailedSigns.stream()
                        .filter(sign -> sign.getDescription().equals(koreanDescription))
                        .findFirst()
                        .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                                "선택한 세부 신체 부분에 해당하지 않는 증상이 포함되어 있습니다."));
                newSignIds.add(detailedSign.getId());
                koreanSigns.add(koreanDescription);
            }
        }

        selectedSign.updateSelectedSign(requestDTO, selectedSBP, newSignIds);
        selectedSignRepository.save(selectedSign);

        List<String> translatedSigns = translationService.translateList(
                selectedSign.getSign(),
                TranslationType.DETAILED_SIGN,
                member.getBasicInfo().getLanguage()
        );

        return SelectedSignResponseDTO.fromEntity(selectedSign, translatedSigns);
    }
}