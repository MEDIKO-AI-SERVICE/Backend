package com.mediko.mediko_server.domain.openai.presentation;


import com.mediko.mediko_server.domain.openai.application.MainBodyPartService;
import com.mediko.mediko_server.domain.openai.dto.response.MainBodyPartResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-body-part")
public class MainBodyPartController {
    private final MainBodyPartService mainBodyPartService;

    // 모든 주신체 부분 조회
    @GetMapping()
    public ResponseEntity<List<MainBodyPartResponseDTO>> findAll() {
        List<MainBodyPartResponseDTO> mainBodyParts = mainBodyPartService.findAll();
        return ResponseEntity.ok(mainBodyParts);
    }
}
