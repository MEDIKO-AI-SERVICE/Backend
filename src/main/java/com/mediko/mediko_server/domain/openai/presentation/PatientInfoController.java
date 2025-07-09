package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.openai.application.PatientInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Tag(name = "patientInfo", description = "타인 정보 입력 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/patientInfo")
public class PatientInfoController {

    private final PatientInfoService patientInfoService;

    // 관계 설정
    @Operation(summary = "타인과의 관계 설정 (선택)", description = "타인일 경우 관계를 입력합니다.")
    @PostMapping("/relation")
    public ResponseEntity<Void> saveRelation(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("relation") String relation,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        patientInfoService.saveRelation(member, sessionId, relation);

        return ResponseEntity.ok().build();
    }

    // 성별 설정
    @Operation(summary = "타인의 성별 설정 (필수)", description = "타인일 경우 성별을 입력합니다.")
    @PostMapping("/gender")
    public ResponseEntity<Void> saveGender(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("gender") Gender gender,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        patientInfoService.saveOtherGender(member, sessionId, gender);

        return ResponseEntity.ok().build();
    }

    // 나이 설정
    @Operation(summary = "타인의 나이 설정 (필수)", description = "타인일 경우 나이를 입력합니다.")
    @PostMapping("/age")
    public ResponseEntity<Void> saveAge(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("age") Integer age,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        patientInfoService.saveOtherAge(member, sessionId, age);

        return ResponseEntity.ok().build();
    }

    // 알레르기 설정
    @Operation(summary = "타인의 알레르기 설정 (선택)", description = "타인일 경우 알레르기를 입력합니다.")
    @PostMapping("/allergy")
    public ResponseEntity<Void> saveAllergy(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("allergy") String allergy,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        patientInfoService.saveOtherAllergy(member, sessionId, allergy);

        return ResponseEntity.ok().build();
    }

    // 가족력 설정
    @Operation(summary = "타인의 가족력 설정 (선택)", description = "타인일 경우 가족력을 입력합니다.")
    @PostMapping("/family-history")
    public ResponseEntity<Void> saveFamilyHistory(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("familyHistory") String familyHistory,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        patientInfoService.saveOtherFamilyHistory(member, sessionId, familyHistory);

        return ResponseEntity.ok().build();
    }

    // 복용 중인 약 설정
    @Operation(summary = "타인의 복용약 설정 (선택)", description = "타인일 경우 복용약을 입력합니다.")
    @PostMapping("/medication")
    public ResponseEntity<Void> saveMedication(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("medication") String medication,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        patientInfoService.saveOtherMedication(member, sessionId, medication);

        return ResponseEntity.ok().build();
    }

    // 과거 병력 설정
    @Operation(summary = "타인의 과거 병력 설정 (선택)", description = "타인일 경우 과거 병력을 입력합니다.")
    @PostMapping("/past-history")
    public ResponseEntity<Void> savePastHistory(
            @RequestParam("sessionId") String sessionId,
            @RequestParam("pastHistory") String pastHistory,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        patientInfoService.saveOtherPastHistory(member, sessionId, pastHistory);

        return ResponseEntity.ok().build();
    }
}
