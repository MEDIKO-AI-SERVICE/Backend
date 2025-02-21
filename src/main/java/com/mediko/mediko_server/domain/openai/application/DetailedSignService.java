package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.openai.domain.DetailedSign;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.domain.repository.DetailedSignRepository;
import com.mediko.mediko_server.domain.openai.domain.repository.SubBodyPartRepository;
import com.mediko.mediko_server.domain.openai.dto.response.DetailedSignResponseDTO;
import com.mediko.mediko_server.global.exception.exceptionType.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.mediko.mediko_server.global.exception.ErrorCode.DATA_NOT_EXIST;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DetailedSignService {
    private final DetailedSignRepository detailedSignRepository;
    private final SubBodyPartRepository subBodyPartRepository;

    // 상세 증상 전체 조회
    public List<DetailedSignResponseDTO> findAll() {
        List<DetailedSign> allDetailedSigns = detailedSignRepository.findAll();
        return allDetailedSigns.stream()
                .map(DetailedSignResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // 상세 중상 부분 조회
    public List<DetailedSignResponseDTO> getDetailedSignsByBodyPart(String bodyPart) {
        SubBodyPart subBodyPart = subBodyPartRepository.findByBody(bodyPart)
                .orElseThrow(() -> new BadRequestException(DATA_NOT_EXIST, "해당하는 신체 부위를 찾을 수 없습니다."));

        List<DetailedSign> detailedSigns = detailedSignRepository.findBySubBodyPart(subBodyPart);

        return detailedSigns.stream()
                .map(DetailedSignResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
