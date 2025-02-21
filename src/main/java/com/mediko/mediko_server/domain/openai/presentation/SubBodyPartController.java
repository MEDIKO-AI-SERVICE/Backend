package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.openai.application.SubBodyPartService;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SubBodyPartResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "sub body", description = "세부 신체 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sub-body")
public class SubBodyPartController {
    private final SubBodyPartService subBodyPartService;

    @Operation(summary = "세부 신체 전체 조회", description = "모든 세부 신체를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<SubBodyPartResponseDTO>> findAll() {
        List<SubBodyPartResponseDTO> subBodyParts = subBodyPartService.findAll();
        return ResponseEntity.ok(subBodyParts);
    }

    @Operation(summary = "세부 신체 부분 조회", description = "선택된 주요 신체에 포함된 모든 세부 신체를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<SubBodyPartResponseDTO>> getSubBodyPartsByBodies(
            @RequestParam(name = "body") List<String> body
    ) {
        List<SubBodyPart> subBodyParts = subBodyPartService.getSubBodyPartsByMainBodyPartBodies(body);
        List<SubBodyPartResponseDTO> response = subBodyParts.stream()
                .map(SubBodyPartResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
