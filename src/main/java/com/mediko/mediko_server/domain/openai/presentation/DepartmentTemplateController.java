package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.DepartmentProcessingState;
import com.mediko.mediko_server.domain.openai.application.DepartmentTemplateService;
import com.mediko.mediko_server.domain.openai.dto.response.DepartmentTemplateResposneDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "department-template", description = "진료과 추천 템플릿 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/department-template")
public class DepartmentTemplateController {

    private final DepartmentTemplateService departmentTemplateService;

    @Operation(summary = "1. 증상 입력 및 세션 생성", description = "증상 입력 및 세션 id를 생성합니다..")
    @PostMapping("/sign")
    public ResponseEntity<String> saveSign(
            @RequestParam("sign") String sign,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        String sessionId = departmentTemplateService.saveSign(member, sign);
        return ResponseEntity.ok(sessionId);
    }

    @Operation(summary = "2. 증상 시작일 입력", description = "증상 시작일을 입력합니다.")
    @PostMapping("/start-date")
    public ResponseEntity<Void> saveStartDate(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("startDate") String startDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        departmentTemplateService.saveStartDate(member, sessionId, startDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3. 통증 강도 입력 및 결과 조회", description = "통증 강도를 입력합니다. 진료과 추천 결과가 조회됩니다.")
    @PostMapping("/intensity")
    public ResponseEntity<DepartmentTemplateResposneDTO> saveIntensityAndGetResult(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("intensity") String intensityDesc,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        DepartmentTemplateResposneDTO response =
                departmentTemplateService.saveIntensityAndGetResult(member, sessionId, intensityDesc);
        return ResponseEntity.ok(response);
    }

//    @Operation(summary = "임시 상태 조회", description = "세션ID로 임시 상태를 조회합니다.")
//    @GetMapping("/state")
//    public ResponseEntity<DepartmentProcessingState> getState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        DepartmentProcessingState state = departmentTemplateService.getState(member, sessionId);
//        return ResponseEntity.ok(state);
//    }
//
//    @Operation(summary = "임시 상태 삭제", description = "세션ID로 임시 상태를 삭제합니다.")
//    @DeleteMapping("/state")
//    public ResponseEntity<Void> clearState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        departmentTemplateService.clearState(member, sessionId);
//        return ResponseEntity.ok().build();
//    }
}
