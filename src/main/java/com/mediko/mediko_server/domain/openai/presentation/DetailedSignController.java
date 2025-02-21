package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.openai.application.DetailedSignService;
import com.mediko.mediko_server.domain.openai.dto.response.DetailedSignResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "detailed sign", description = "상세 증상 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/detailed-sign")
public class DetailedSignController {
    private final DetailedSignService detailedSignService;

    @Operation(summary = "상세 증상 전체 조회", description = "모든 상세 증상을 조회합니다.")
    @GetMapping("/all")
    public List<DetailedSignResponseDTO> getAllDetailedSigns() {
        return detailedSignService.findAll();
    }

    @Operation(summary = "상세 중상 부분 조회", description = "선택된 세부 신체에 포함된 모든 상세 증상을 조회합니다.")
    @GetMapping
    public List<DetailedSignResponseDTO> getDetailedSignsBySubBodyPart(
            @RequestParam(name = "body") String body) {
        return detailedSignService.getDetailedSignsByBodyPart(body);
    }
}
