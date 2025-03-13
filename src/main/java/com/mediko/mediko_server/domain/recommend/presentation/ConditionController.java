package com.mediko.mediko_server.domain.recommend.presentation;

import com.mediko.mediko_server.domain.recommend.application.ConditionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "condition", description = "응급실 특수 상태 API")
@RestController
@RequestMapping("/api/v1/condition")
@RequiredArgsConstructor
public class ConditionController {

    private final ConditionService conditionService;

    @GetMapping
    @Operation(summary = "응급실 특수상태 전체 조회", description = "응급실에서 선택할 수 있는 모든 특수상태를 조회합니다.")
    public ResponseEntity<List<String>> getAllConditions() {
        List<String> conditions = conditionService.getAllConditionDescriptions();
        return ResponseEntity.ok(conditions);
    }
}
