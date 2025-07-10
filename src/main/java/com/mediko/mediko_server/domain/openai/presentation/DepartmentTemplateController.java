package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.DepartmentTemplateService;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.domain.openai.dto.request.AdditionalRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSignRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.SuggestSignRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.DepartmentTemplateResposneDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "department-template", description = "진료과 추천 템플릿 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/department-template")
public class DepartmentTemplateController {

    private final DepartmentTemplateService departmentTemplateService;

    @Operation(summary = "1. 신체 부위 입력 및 세션 생성", description = "신체 부위를 입력하면 세션ID와 증상 후보(adjectives) 리스트가 반환됩니다.")
    @PostMapping("/body-part")
    public ResponseEntity<Map<String, Object>> saveBodyPart(
            @RequestBody SuggestSignRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        Map<String, Object> result = departmentTemplateService.saveBodyPart(member, requestDTO);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "2. 증상 후보/직접입력 저장", description = "증상 후보에서 복수 선택하거나 직접 입력한 증상 리스트를 저장합니다.")
    @PostMapping("/selected-sign")
    public ResponseEntity<Void> saveSelectedSign(
            @RequestParam("sessionId") String sessionId,
            @RequestBody SelectedSignRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        departmentTemplateService.saveSelectedSign(member, sessionId, requestDTO);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "3. 증상 시작일 입력", description = "증상 시작일을 저장합니다.")
    @PostMapping("/start-date")
    public ResponseEntity<Void> saveStartDate(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("startDate") String startDate,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        departmentTemplateService.saveStartDate(member, sessionId, startDate);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "4. 통증 강도 입력", description = "통증 강도를 저장합니다.")
    @PostMapping("/intensity")
    public ResponseEntity<Void> saveIntensity(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("intensity") Intensity intensity,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        departmentTemplateService.saveIntensity(member, sessionId, intensity);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "5. 추가 정보 입력",
               description = "hasAdditional=true면 body에 추가 정보를 입력, false면 입력 없이 결과를 반환합니다.")
    @PostMapping("/additional")
    public ResponseEntity<DepartmentTemplateResposneDTO> saveAdditional(
            @RequestParam("hasAdditional") boolean hasAdditional,
            @RequestParam("sessionId") String sessionId,
            @RequestBody AdditionalRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        DepartmentTemplateResposneDTO result = departmentTemplateService.saveAdditionalAndReturnResult(
                member, sessionId, hasAdditional, requestDTO);
        return ResponseEntity.ok(result);
    }


//    // 상태 조회 (선택)
//    @GetMapping("/state")
//    public ResponseEntity<?> getState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        return ResponseEntity.ok(departmentTemplateService.getState(member, sessionId));
//    }
//
//    // 상태 삭제 (선택)
//    @DeleteMapping("/state")
//    public ResponseEntity<Void> clearState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        departmentTemplateService.clearState(member, sessionId);
//        return ResponseEntity.ok().build();
//    }
}
