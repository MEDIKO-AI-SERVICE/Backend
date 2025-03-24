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


    private List<DetailedSign> getValidDetailedSigns(SelectedSBP selectedSBP, List<String> descriptions, Member member) {
        // selectedSBP의 sbpIds가 비어있는지 체크
        if (selectedSBP.getSbpIds() == null || selectedSBP.getSbpIds().isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "선택된 신체 부위가 없습니다.");
        }

        if (descriptions == null || descriptions.isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "선택된 증상이 없습니다.");
        }

        List<DetailedSign> selectedDetailedSigns = detailedSignRepository
                .findBySubBodyPartIdIn(selectedSBP.getSbpIds());

        if (selectedDetailedSigns.isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER,
                    "선택한 신체 부위에 등록된 증상이 없습니다.");
        }

        try {
            Map<String, String> translationMap = selectedDetailedSigns.stream()
                    .collect(Collectors.toMap(
                            sign -> translationService.translate(sign.getDescription(),
                                    TranslationType.DETAILED_SIGN,
                                    member.getBasicInfo().getLanguage()),
                            DetailedSign::getDescription
                    ));

            List<String> koreanDescriptions = descriptions.stream()
                    .map(desc -> translationMap.getOrDefault(desc, desc))
                    .collect(Collectors.toList());

            log.info("Original descriptions: {}", descriptions);
            log.info("Korean descriptions: {}", koreanDescriptions);

            List<DetailedSign> validSigns = selectedDetailedSigns.stream()
                    .filter(sign -> koreanDescriptions.contains(sign.getDescription()))
                    .collect(Collectors.toList());

            if (validSigns.isEmpty()) {
                throw new BadRequestException(INVALID_PARAMETER,
                        "선택한 세부 신체 부분에 해당하는 증상을 찾을 수 없습니다.");
            }

            return validSigns;

        } catch (Exception e) {
            if (!(e instanceof BadRequestException)) {
                log.error("증상 처리 중 오류 발생: {}", e.getMessage());
                throw new BadRequestException(INVALID_PARAMETER,
                        "증상 처리 중 오류가 발생했습니다.");
            }
            throw e;
        }
    }

    @Transactional
    public SelectedSignResponseDTO saveSelectedSign(
            Member member, SelectedSignRequestDTO requestDTO, Long selectedSBPId) {

        SelectedSBP selectedSBP = selectedSBPRepository.findById(selectedSBPId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "선택한 세부 신체 부분이 존재하지 않습니다."));

        List<DetailedSign> validDetailedSigns = getValidDetailedSigns(selectedSBP, requestDTO.getDescription(), member);

        List<String> koreanSigns = validDetailedSigns.stream()
                .map(DetailedSign::getDescription)
                .collect(Collectors.toList());

        List<Long> selectedSignIds = validDetailedSigns.stream()
                .map(DetailedSign::getId)
                .collect(Collectors.toList());

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
            List<DetailedSign> validDetailedSigns = getValidDetailedSigns(selectedSBP, requestDTO.getDescription(), member);

            koreanSigns = validDetailedSigns.stream()
                    .map(DetailedSign::getDescription)
                    .collect(Collectors.toList());

            newSignIds = validDetailedSigns.stream()
                    .map(DetailedSign::getId)
                    .collect(Collectors.toList());
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