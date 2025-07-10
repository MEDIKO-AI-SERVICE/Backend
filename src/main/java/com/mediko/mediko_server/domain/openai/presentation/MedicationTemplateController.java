package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.MedicationTemplateService;
import com.mediko.mediko_server.domain.openai.dto.response.MedicationTemplateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "medication-template", description = "약 추천 템플릿 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medication-template")
public class MedicationTemplateController {

    private final MedicationTemplateService medicationTemplateService;

    // 1. isSelf 설정 + 세션 생성
    @Operation(summary = "1. 본인/타인 여부 설정",
            description = "본인/타인 여부를 선택하면 세션 id가 반환됩니다. false인 경우 이후 타인 정보를 입력해야합니다.")
    @PostMapping("/is-self")
    public ResponseEntity<Map<String, String>> saveIsSelf(
            @RequestParam("isSelf") boolean isSelf,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        String sessionId = medicationTemplateService.saveIsSelf(member, isSelf);
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }

    // 2. 증상 설명 입력 -> 결과 반환
    @Operation(summary = "2. 증상 설명 입력 및 결과 반환", description = "증상 설명을 입력하면 약 추천 결과가 반환됩니다.")
    @PostMapping("/sign")
    public ResponseEntity<MedicationTemplateResponseDTO> saveSign(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("sign") String sign,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        MedicationTemplateResponseDTO response = medicationTemplateService.saveSign(member, sessionId, sign);
        return ResponseEntity.ok(response);
    }


//    // 상태 조회 (선택)
//    @GetMapping("/state")
//    public ResponseEntity<?> getState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        return ResponseEntity.ok(medicationTemplateService.getState(member, sessionId));
//    }
//
//    // 상태 삭제 (선택)
//    @DeleteMapping("/state")
//    public ResponseEntity<Void> clearState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        medicationTemplateService.clearState(member, sessionId);
//        return ResponseEntity.ok().build();
//    }
}
