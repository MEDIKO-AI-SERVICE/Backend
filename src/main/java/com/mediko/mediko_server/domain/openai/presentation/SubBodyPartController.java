package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.openai.application.SubBodyPartService;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SubBodyPartResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sub-body-part")
public class SubBodyPartController {
    private final SubBodyPartService subBodyPartService;

    // 모든 세부신체 부분 조회
    @GetMapping()
    public ResponseEntity<List<SubBodyPartResponseDTO>> findAll() {
        List<SubBodyPartResponseDTO> subBodyParts = subBodyPartService.findAll();
        return ResponseEntity.ok(subBodyParts);
    }

    //선택된 주신체 부분에 포함된 모든 세부신체 부분 조회
    @PostMapping
    public ResponseEntity<List<SubBodyPartResponseDTO>> getSubBodyPartsByBodies(
            @RequestBody SelectedSBPRequestDTO requestBody
    ) {
        List<SubBodyPart> subBodyParts = subBodyPartService.getSubBodyPartsByMainBodyPartBodies(requestBody.getBody());
        List<SubBodyPartResponseDTO> response = subBodyParts.stream()
                .map(SubBodyPartResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
