package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.SelectedMBP;
import com.mediko.mediko_server.domain.openai.domain.repository.MainBodyPartRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SelectedMBPRepository;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedMBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedMBPResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedSBPResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    private final MainBodyPartRepository mainBodyPartRepository;

    // SelectedMBP 저장
    @Transactional
    public SelectedMBPResponseDTO saveSelectedMBP(SelectedMBPRequestDTO requestDTO, Member member) {
        List<String> mainBodyPartNames = requestDTO.getBody();

        if (mainBodyPartNames == null || mainBodyPartNames.isEmpty()) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 선택해야 합니다.");
        }
        if (mainBodyPartNames.size() > 2) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 2개 이내로 선택하세요.");
        }

        List<String> bodyList = new ArrayList<>();
        List<Long> mbpIds = new ArrayList<>();

        for (String mainBodyPartName : mainBodyPartNames) {
            MainBodyPart foundMainBodyPart = mainBodyPartRepository.findByBody(mainBodyPartName)
                    .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST,
                            String.format("주 신체 부분 '%s'이(가) 존재하지 않습니다.", mainBodyPartName)));

            bodyList.add(mainBodyPartName);
            mbpIds.add(foundMainBodyPart.getId());
        }

        SelectedMBP selectedMBP = SelectedMBP.builder()
                .body(bodyList)
                .member(member)
                .mbpIds(mbpIds)
                .build();

        selectedMBP = selectedMBPRepository.save(selectedMBP);

        return SelectedMBPResponseDTO.fromEntity(selectedMBP);
    }



    // 최신 SelectedMBP 조회
    public SelectedMBPResponseDTO getLatestSelectedSBP(Member member) {
        SelectedMBP latestSelectedMBP = selectedMBPRepository.findLatestByMemberId(member.getId())
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "선택된 신체 부분이 없습니다."));

        return SelectedMBPResponseDTO.fromEntity(latestSelectedMBP);
    }


    // 최신 SelectedMBP 수정
    @Transactional
    public SelectedMBPResponseDTO updateLatestSelectedSBP(SelectedMBPRequestDTO requestDTO, Member member) {
        if (requestDTO.getBody() == null || requestDTO.getBody().size() > 2) {
            throw new BadRequestException(INVALID_PARAMETER, "통증이 있는 부위를 2개 이내로 선택하세요.");
        }

        List<MainBodyPart> validSubBodyParts = requestDTO.getBody().stream()
                .map(body -> mainBodyPartRepository.findByBody(body)
                        .orElseThrow(() -> new BadRequestException(INVALID_PARAMETER,
                                String.format("요청한 부위 '%s'가 유효하지 않습니다.", body))))
                .collect(Collectors.toList());

        List<Long> mbpIds = validSubBodyParts.stream()
                .map(MainBodyPart::getId)
                .collect(Collectors.toList());

        SelectedMBP selectedMBP = selectedMBPRepository.findLatestByMemberId(member.getId())
                .orElseThrow(() -> new RuntimeException("등록된 주신체 부분을 찾을 수 없습니다."));

        selectedMBP.updateSelectedMBP(requestDTO, mbpIds);

        selectedMBPRepository.save(selectedMBP);

        return SelectedMBPResponseDTO.fromEntity(selectedMBP);
    }

}
