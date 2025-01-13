package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.openai.application.SubBodyPartService;
import com.mediko.mediko_server.domain.openai.dto.response.SubBodyPartResponseDTO;
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
@RequestMapping("/api/v1/sub-body-part")
public class SubBodyPartController {
    private final SubBodyPartService subBodyPartService;

    // 모든 SubBodyPart 조회
    @GetMapping()
    public ResponseEntity<List<SubBodyPartResponseDTO>> findAll() {
        List<SubBodyPartResponseDTO> subBodyParts = subBodyPartService.findAll();
        return ResponseEntity.ok(subBodyParts);
    }
}
