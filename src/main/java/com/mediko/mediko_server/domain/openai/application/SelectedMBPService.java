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
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    // 선택한 주요 신체 저장
    @Transactional
    public SelectedMBPResponseDTO saveSelectedMBP(SelectedMBPRequestDTO requestDTO, Member member) {
        List<String> mainBodyPartNames = requestDTO.getBody();

        if (mainBodyPartNames == null || mainBodyPartNames.isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 선택해야 합니다.");
        }
        if (mainBodyPartNames.size() > 2) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 2개 이내로 선택하세요.");
        }

        List<MainBodyPart> foundMainBodyParts = mainBodyPartRepository.findByBodyIn(mainBodyPartNames);

        if (foundMainBodyParts.size() != mainBodyPartNames.size()) {
            throw new BadRequestException(DATA_NOT_EXIST, "존재하지 않는 주 신체 부분이 포함되어 있습니다.");
        }

        List<Long> mbpIds = foundMainBodyParts.stream()
                .map(MainBodyPart::getId)
                .collect(Collectors.toList());

        SelectedMBP selectedMBP = requestDTO.toEntity()
                .toBuilder()
                .member(member)
                .mbpIds(mbpIds)
                .build();

        selectedMBP = selectedMBPRepository.save(selectedMBP);

        return SelectedMBPResponseDTO.fromEntity(selectedMBP, mainBodyPartRepository);
    }

    //선택한 주요 신체 조회
    public SelectedMBPResponseDTO getSelectedMBP(Long selectedMbpId, Member member) {
        SelectedMBP selectedMBP = selectedMBPRepository.findByIdAndMember(selectedMbpId, member)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "선택된 신체 부분이 없습니다."));

        return SelectedMBPResponseDTO.fromEntity(selectedMBP, mainBodyPartRepository);
    }

    //선택한 주요 신체 수정
    @Transactional
    public SelectedMBPResponseDTO updateSelectedMBP(Long selectedMbpId, SelectedMBPRequestDTO requestDTO, Member member) {
        SelectedMBP selectedMBP = selectedMBPRepository.findById(selectedMbpId)
                .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER, "선택된 Main Body Part가 존재하지 않습니다."));

        List<MainBodyPart> mainBodyParts = mainBodyPartRepository.findByBodyIn(requestDTO.getBody());
        if (mainBodyParts.size() != requestDTO.getBody().size()) {
            throw new BadRequestException(INVALID_PARAMETER, "존재하지 않는 Body 값이 포함되어 있습니다.");
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

        return SelectedMBPResponseDTO.fromEntity(selectedMBP, mainBodyPartRepository);
    }
}
