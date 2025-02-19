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

    @Transactional
    public SelectedSignResponseDTO saveSelectedSign(
            Member member, SelectedSignRequestDTO requestDTO, Long selectedSBPId) {

        // 1. selectedSBPId로 선택된 SelectedSBP 찾기
        SelectedSBP selectedSBP = selectedSBPRepository.findById(selectedSBPId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "선택한 세부 신체 부분이 존재하지 않습니다."));

        // 2. 해당 SubBodyPart에 속한 DetailedSign들 조회
        List<DetailedSign> validDetailedSigns = detailedSignRepository.findBySubBodyPartIdIn(selectedSBP.getSbpIds());

        // 3. validDetailedSigns를 Map으로 변환 (sign을 key로, DetailedSign을 value로)
        Map<String, DetailedSign> signToDetailedSign = validDetailedSigns.stream()
                .collect(Collectors.toMap(DetailedSign::getSign, sign -> sign));

        // 4. 요청된 sign들이 유효한지 검증하고, 해당하는 signId들 수집
        List<Long> selectedSignIds = new ArrayList<>();
        for (String sign : requestDTO.getSign()) {
            DetailedSign detailedSign = signToDetailedSign.get(sign);
            if (detailedSign == null) {
                throw new BadRequestException(INVALID_PARAMETER, "선택한 세부 신체 부분에 해당하지 않는 증상이 포함되어 있습니다.");
            }
            selectedSignIds.add(detailedSign.getId());
        }

        // 5. SelectedSign 객체 생성
        SelectedSign selectedSign = requestDTO.toEntity()
                .toBuilder()
                .signIds(selectedSignIds)
                .selectedSBP(selectedSBP)
                .member(member)
                .build();

        // 6. SelectedSign 저장
        selectedSignRepository.save(selectedSign);

        // 7. Response DTO 반환
        return SelectedSignResponseDTO.fromEntity(selectedSign, selectedSBP);
    }


    public SelectedSignResponseDTO getSelectedSign(
            Long selectedDetailedSignId, Member member
    ) {
        SelectedSign selectedSign = selectedSignRepository
                .findByIdAndMember(selectedDetailedSignId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 상세 증상을 찾을 수 없습니다."));

        return SelectedSignResponseDTO.fromEntity(selectedSign, selectedSign.getSelectedSBP());
    }

    @Transactional
    public SelectedSignResponseDTO updateSelectedSign(
            SelectedSignRequestDTO requestDTO, Long selectedSignId, Member member
    ) {
        // 1. 기존 SelectedDetailedSign 조회
        SelectedSign selectedSign = selectedSignRepository
                .findByIdAndMember(selectedSignId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 상세 증상을 찾을 수 없습니다."));

        // 2. 기존에 연결된 SelectedSBP 사용
        SelectedSBP selectedSBP = selectedSign.getSelectedSBP();

        // 3. sign 유효성 검증
        List<Long> newSignIds = new ArrayList<>();
        if (requestDTO.getSign() != null && !requestDTO.getSign().isEmpty()) {
            // 3-1. 해당 SubBodyPart에 속한 DetailedSign들 조회
            List<DetailedSign> validDetailedSigns = detailedSignRepository
                    .findBySubBodyPartIdIn(selectedSBP.getSbpIds());

            // 3-2. 유효한 sign들의 Map 생성
            Map<String, DetailedSign> signToDetailedSign = validDetailedSigns.stream()
                    .collect(Collectors.toMap(DetailedSign::getSign, sign -> sign));

            // 3-3. 요청된 sign들의 유효성 검증 및 signId 수집
            for (String sign : requestDTO.getSign()) {
                DetailedSign detailedSign = signToDetailedSign.get(sign);
                if (detailedSign == null) {
                    throw new BadRequestException(INVALID_PARAMETER,
                            "선택한 세부 신체 부분에 해당하지 않는 증상이 포함되어 있습니다: " + sign);
                }
                newSignIds.add(detailedSign.getId());
            }
        }

        // 3. SelectedDetailedSign 업데이트
        selectedSign.updateSelectedSign(requestDTO, selectedSBP, newSignIds);

        // 4. 변경사항 저장
        selectedSignRepository.save(selectedSign);

        return SelectedSignResponseDTO.fromEntity(selectedSign, selectedSBP);
    }
}