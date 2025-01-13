package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.SelectedSBP;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.domain.repository.MainBodyPartRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedMBPRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedSBPRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SubBodyPartRepository;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedSBPResponseDTO;
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
    private final SelectedMBPRepository selectedMBPRepository;
    private final SubBodyPartRepository subBodyPartRepository;
    private final MainBodyPartRepository mainBodyPartRepository;

    //selectedMBP에 포함된 selectedSBP 조회
    @Transactional
    public List<SubBodyPart> getSubBodyPartsByMainBodyPartBodies(List<String> bodies) {
        return subBodyPartRepository.findAllByMainBodyPartBodies(bodies);
    }

    //selectedSBP 저장
    @Transactional
    public SelectedSBPResponseDTO saveSelectedSBP(Member member, SelectedSBPRequestDTO requestDTO) {
        List<SubBodyPart> validSubBodyParts = requestDTO.getBody().stream()
                .map(body -> subBodyPartRepository.findByBody(body)
                        .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                                String.format("요청한 부위 '%s'가 유효하지 않습니다.", body))))
                .collect(Collectors.toList());

        List<Long> sbpIds = validSubBodyParts.stream()
                .map(SubBodyPart::getId)
                .collect(Collectors.toList());
        Long mbpId = validSubBodyParts.get(0).getMainBodyPart().getId();

        SelectedSBP selectedSBP = SelectedSBP.builder()
                .body(requestDTO.getBody())
                .sbpIds(sbpIds)
                .mbpId(mbpId)
                .member(member)
                .build();

        selectedSBPRepository.save(selectedSBP);

        return SelectedSBPResponseDTO.fromEntity(selectedSBP);
    }

    // 최신 SelectedSBP 조회
    public SelectedSBPResponseDTO getLatestSelectedSBP(Member member) {
        SelectedSBP selectedSBP = selectedSBPRepository.findLatestByMemberId(member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "최신 세부 신체 부분을 찾을 수 없습니다."));

        return SelectedSBPResponseDTO.fromEntity(selectedSBP);
    }

    // 최신 SelectedSBP 수정
    @Transactional
    public SelectedSBPResponseDTO updateLatestSelectedSBP(Member member, SelectedSBPRequestDTO requestDTO) {
        List<SubBodyPart> validSubBodyParts = requestDTO.getBody().stream()
                .map(body -> subBodyPartRepository.findByBody(body)
                        .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                                String.format("요청한 부위 '%s'가 유효하지 않습니다.", body))))
                .collect(Collectors.toList());

        List<Long> sbpIds = validSubBodyParts.stream()
                .map(SubBodyPart::getId)
                .collect(Collectors.toList());

        SelectedSBP selectedSBP = selectedSBPRepository.findLatestByMemberId(member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "최신 세부 신체 부분을 찾을 수 없습니다."));

        selectedSBP.updateSelectedSBP(requestDTO, sbpIds);

        selectedSBPRepository.save(selectedSBP);

        return SelectedSBPResponseDTO.fromEntity(selectedSBP);
    }

}
