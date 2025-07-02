package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
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

    // isSelf 설정 + 세션 생성
    @Operation(summary = "1. 본인/타인 여부 설정", description = "본인/타인 여부를 선택 및 세션을 생성합니다.")
    @PostMapping("/is-self")
    public ResponseEntity<Map<String, String>> saveIsSelf(
            @RequestParam("isSelf") boolean isSelf,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        String sessionId = medicationTemplateService.saveIsSelf(member, isSelf);
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }

    // 관계 설정
    @Operation(summary = "2. 타인과의 관계 설정 (선택)", description = "타인일 경우 관계를 입력합니다.")
    @PostMapping("/relation")
    public ResponseEntity<Void> saveRelation(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("relation") String relation,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateRelation(member, sessionId, relation);
        return ResponseEntity.ok().build();
    }

    // 성별 설정
    @Operation(summary = "2. 타인의 성별 설정 (필수)", description = "타인일 경우 성별을 입력합니다.")
    @PostMapping("/gender")
    public ResponseEntity<Void> saveGender(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("gender") Gender gender,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateGender(member, sessionId, gender);
        return ResponseEntity.ok().build();
    }

    // 나이 설정
    @Operation(summary = "2. 타인의 나이 설정 (필수)", description = "타인일 경우 나이를 입력합니다.")
    @PostMapping("/age")
    public ResponseEntity<Void> saveAge(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("age") Integer age,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateAge(member, sessionId, age);
        return ResponseEntity.ok().build();
    }

    // 알레르기 설정
    @Operation(summary = "2. 타인의 알레르기 설정 (선택)", description = "타인일 경우 알레르기를 입력합니다.")
    @PostMapping("/allergy")
    public ResponseEntity<Void> saveAllergy(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("allergy") String allergy,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateAllergy(member, sessionId, allergy);
        return ResponseEntity.ok().build();
    }

    // 가족력 설정
    @Operation(summary = "2. 타인의 가족력 설정 (선택)", description = "타인일 경우 가족력을 입력합니다.")
    @PostMapping("/family-history")
    public ResponseEntity<Void> saveFamilyHistory(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("familyHistory") String familyHistory,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateFamilyHistory(member, sessionId, familyHistory);
        return ResponseEntity.ok().build();
    }

    // 복용 중인 약 설정
    @Operation(summary = "2. 타인의 복용약 설정 (선택)", description = "타인일 경우 복용약을 입력합니다.")
    @PostMapping("/medication")
    public ResponseEntity<Void> saveMedication(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("medication") String medication,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateMedication(member, sessionId, medication);
        return ResponseEntity.ok().build();
    }

    // 과거 병력 설정
    @Operation(summary = "2. 타인의 과거 병력 설정 (선택)", description = "타인일 경우 과거 병력을 입력합니다.")
    @PostMapping("/past-history")
    public ResponseEntity<Void> savePastHistory(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("pastHistory") String pastHistory,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updatePastHistory(member, sessionId, pastHistory);
        return ResponseEntity.ok().build();
    }


    @Operation(summary = "3. 증상 설명 입력", description = "증상 설명을 입력합니다. 약 추천 결과가 조회됩니다.")
    @PostMapping("/sign")
    public ResponseEntity<MedicationTemplateResponseDTO> saveSign(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("sign") String sign,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        MedicationTemplateResponseDTO response =
                medicationTemplateService.saveSign(member, sessionId, sign);

        return ResponseEntity.ok(response);
    }


//    // 상태 조회
//    @GetMapping("/state")
//    public ResponseEntity<MedicationProcessingState> getState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        MedicationProcessingState state = medicationTemplateService.getState(member, sessionId);
//        return ResponseEntity.ok(state);
//    }
//
//    // 상태 삭제
//    @DeleteMapping("/state")
//    public ResponseEntity<Void> clearState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        medicationTemplateService.clearState(member, sessionId);
//        return ResponseEntity.ok().build();
//    }
}
