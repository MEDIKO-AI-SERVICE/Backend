package com.mediko.mediko_server.domain.openai.application;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.repository.MainBodyPartRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedMBPRepository;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedMBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedMBPResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;
import static com.mediko.mediko_server.global.exception.ErrorCode.INVALID_PARAMETER;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SelectedMBPService {

    private final SelectedMBPRepository selectedMBPRepository;
    private final MainBodyPartRepository mainBodyPartRepository;
    private final ObjectMapper objectMapper;
    private final EntityManager entityManager;

    // SelectedMBP 저장
    @Transactional
    public SelectedMBPResponseDTO selectMainBodyPart(SelectedMBPRequestDTO requestDTO, Member member) {
        List<String> mainBodyPartNames = requestDTO.getBody();

        if (mainBodyPartNames.size() > 2) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 2개 이내로 선택하세요.");
        }

        List<String> bodyList = new ArrayList<>();

        for (String mainBodyPartName : mainBodyPartNames) {
            MainBodyPart mainBodyPart = mainBodyPartRepository.findByBody(mainBodyPartName)
                    .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 주 신체 부분이 존재하지 않습니다."));

            bodyList.add(mainBodyPartName);
        }

        SelectedMBP selectedMBP = SelectedMBP.builder()
                .body(bodyList)
                .member(member)
                .build();

        selectedMBP = selectedMBPRepository.save(selectedMBP);

        return SelectedMBPResponseDTO.fromEntity(selectedMBP);
    }

    // SelectedMBP 조회
    public SelectedMBPResponseDTO getSelectedMBP(Member member) {

        // Optional을 사용하여 조회
        SelectedMBP latestSelectedMBP = selectedMBPRepository.findTopByMemberOrderByCreatedAtDesc(member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "선택된 신체 부분이 없습니다."));

        return SelectedMBPResponseDTO.fromEntity(latestSelectedMBP);
    }


    // SelectedMBP 수정
    @Transactional
    public SelectedMBPResponseDTO updateSelectedMBP(SelectedMBPRequestDTO requestDTO, Member member) {
        if (requestDTO.getBody().size() > 2) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 2개 이내로 선택하세요.");
        }

        // member.getId()를 전달
        SelectedMBP latestSelectedMBP = selectedMBPRepository.findTopByMemberOrderByCreatedAtDesc(member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "수정할 신체 부분 데이터가 없습니다."));

        latestSelectedMBP.updateSelectedMBP(requestDTO);

        return SelectedMBPResponseDTO.fromEntity(latestSelectedMBP);
    }
}
