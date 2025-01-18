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


    @Transactional
    public SelectedSBPResponseDTO saveSelectedSBP(Member member, SelectedSBPRequestDTO requestDTO, Long selectedMBPId) {
        List<SubBodyPart> validSubBodyParts = requestDTO.getBody().stream()
                .map(body -> subBodyPartRepository.findByBody(body)
                        .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                                String.format("'%s' 부분은 존재하지 않습니다.", body))))
                .collect(Collectors.toList());

        List<Long> sbpIds = validSubBodyParts.stream()
                .map(SubBodyPart::getId)
                .collect(Collectors.toList());

        // 3. selectedMBPId로 선택된 SelectedMBP 찾기
        SelectedMBP selectedMBP = selectedMBPRepository.findById(selectedMBPId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "선택한 주신체 부분이 존재하지 않습니다."));

        // 4. SelectedSBP 객체 생성
        SelectedSBP selectedSBP = requestDTO.toEntity()
                .toBuilder()
                .sbpIds(sbpIds)
                .selectedMBP(selectedMBP)
                .member(member)
                .build();

        // 5. SelectedSBP 저장
        selectedSBPRepository.save(selectedSBP);

        // 6. Response DTO 반환
        return SelectedSBPResponseDTO.fromEntity(selectedSBP);
    }



    // SelectedSBP 조회
    public SelectedSBPResponseDTO getSelectedSBP(Long selectedSBPId, Member member) {
        SelectedSBP selectedSBP = selectedSBPRepository.findByIdAndMember(selectedSBPId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 세부 신체 부분을 찾을 수 없습니다."));

        return SelectedSBPResponseDTO.fromEntity(selectedSBP);
    }

    // SelectedSBP 수정
    @Transactional
    public SelectedSBPResponseDTO updateSelectedSBP(Long selectedSBPId, Member member, SelectedSBPRequestDTO requestDTO) {
        // 요청받은 body part들을 검증
        List<SubBodyPart> validSubBodyParts = requestDTO.getBody().stream()
                .map(body -> subBodyPartRepository.findByBody(body)
                        .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                                String.format("'%s' 부분은 존재하지 않습니다.", body))))
                .collect(Collectors.toList());

        List<Long> sbpIds = validSubBodyParts.stream()
                .map(SubBodyPart::getId)
                .collect(Collectors.toList());

        // 기존에 저장된 SelectedSBP를 가져옴
        SelectedSBP selectedSBP = selectedSBPRepository.findByIdAndMember(selectedSBPId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당 세부 신체 부분을 찾을 수 없습니다."));

        // 기존 SelectedSBP에서 연결된 SelectedMBP 사용
        SelectedMBP selectedMBP = selectedSBP.getSelectedMBP(); // 이미 연결된 SelectedMBP 사용

        // SelectedSBP 수정
        selectedSBP.updateSelectedSBP(requestDTO, sbpIds, selectedMBP);

        // 변경된 SelectedSBP 저장
        selectedSBPRepository.save(selectedSBP);

        return SelectedSBPResponseDTO.fromEntity(selectedSBP);
    }

}
