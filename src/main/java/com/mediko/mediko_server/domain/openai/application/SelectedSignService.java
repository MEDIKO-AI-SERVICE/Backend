package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import com.mediko.mediko_server.domain.openai.domain.SelectedSign;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.domain.repository.DetailedSignRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedSignRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedSBPRepository;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSignRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedSignResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    //선택한 상세 증상 저장
    @Transactional
    public SelectedSignResponseDTO saveSelectedSign(
            Member member, SelectedSignRequestDTO requestDTO, Long selectedSBPId) {

        SelectedSBP selectedSBP = selectedSBPRepository.findById(selectedSBPId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "선택한 세부 신체 부분이 존재하지 않습니다."));

        List<DetailedSign> validDetailedSigns = detailedSignRepository.findBySubBodyPartIdIn(selectedSBP.getSbpIds());

        Map<String, DetailedSign> signToDetailedSign = validDetailedSigns.stream()
                .collect(Collectors.toMap(DetailedSign::getSign, sign -> sign));

        List<Long> selectedSignIds = new ArrayList<>();
        for (String sign : requestDTO.getSign()) {
            DetailedSign detailedSign = signToDetailedSign.get(sign);
            if (detailedSign == null) {
                throw new BadRequestException(INVALID_PARAMETER, "선택한 세부 신체 부분에 해당하지 않는 증상이 포함되어 있습니다.");
            }
            selectedSignIds.add(detailedSign.getId());
        }

        SelectedSign selectedSign = requestDTO.toEntity()
                .toBuilder()
                .signIds(selectedSignIds)
                .selectedSBP(selectedSBP)
                .member(member)
                .build();

        selectedSignRepository.save(selectedSign);

        return SelectedSignResponseDTO.fromEntity(selectedSign);
    }

    //선택한 상세 증상 조회
    public SelectedSignResponseDTO getSelectedSign(
            Long selectedDetailedSignId, Member member
    ) {
        SelectedSign selectedSign = selectedSignRepository
                .findByIdAndMember(selectedDetailedSignId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 상세 증상을 찾을 수 없습니다."));

        return SelectedSignResponseDTO.fromEntity(selectedSign);
    }

    //선택한 상세 증상 수정
    @Transactional
    public SelectedSignResponseDTO updateSelectedSign(
            SelectedSignRequestDTO requestDTO, Long selectedSignId, Member member
    ) {
        SelectedSign selectedSign = selectedSignRepository
                .findByIdAndMember(selectedSignId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 상세 증상을 찾을 수 없습니다."));

        SelectedSBP selectedSBP = selectedSign.getSelectedSBP();

        List<Long> newSignIds = new ArrayList<>();
        if (requestDTO.getSign() != null && !requestDTO.getSign().isEmpty()) {
            List<DetailedSign> validDetailedSigns = detailedSignRepository
                    .findBySubBodyPartIdIn(selectedSBP.getSbpIds());

            Map<String, DetailedSign> signToDetailedSign = validDetailedSigns.stream()
                    .collect(Collectors.toMap(DetailedSign::getSign, sign -> sign));

            for (String sign : requestDTO.getSign()) {
                DetailedSign detailedSign = signToDetailedSign.get(sign);
                if (detailedSign == null) {
                    throw new BadRequestException(INVALID_PARAMETER,
                            "선택한 세부 신체 부분에 해당하지 않는 증상이 포함되어 있습니다: " + sign);
                }
                newSignIds.add(detailedSign.getId());
            }
        }

        selectedSign.updateSelectedSign(requestDTO, selectedSBP, newSignIds);
        selectedSignRepository.save(selectedSign);

        return SelectedSignResponseDTO.fromEntity(selectedSign);
    }
}