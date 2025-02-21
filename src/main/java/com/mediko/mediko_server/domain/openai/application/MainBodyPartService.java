package com.mediko.mediko_server.domain.openai.application;

import com.mediko.mediko_server.domain.openai.domain.MainBodyPart;
import com.mediko.mediko_server.domain.openai.domain.repository.MainBodyPartRepository;
import com.mediko.mediko_server.domain.openai.dto.response.MainBodyPartResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MainBodyPartService {
    private final MainBodyPartRepository mainBodyPartRepository;

    // 주요 신체 전체 조회
    public List<MainBodyPartResponseDTO> findAll() {
        List<MainBodyPart> allMainBodyParts = mainBodyPartRepository.findAll();
        return allMainBodyParts.stream()
                .map(MainBodyPartResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
