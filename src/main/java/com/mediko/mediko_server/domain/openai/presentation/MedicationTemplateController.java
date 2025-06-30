package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Gender;
import com.mediko.mediko_server.domain.openai.application.MedicationProcessingState;
import com.mediko.mediko_server.domain.openai.application.MedicationTemplateService;
import com.mediko.mediko_server.domain.openai.dto.request.MedicationTemplateRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.MedicationTemplateResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "medication-template", description = "약물 템플릿(약 추천) API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/medication-template")
public class MedicationTemplateController {

    private final MedicationTemplateService medicationTemplateService;

    @PostMapping("/is-self")
    public ResponseEntity<Void> saveIsSelf(@RequestParam boolean isSelf,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.saveIsSelf(member, isSelf);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/relation")
    public ResponseEntity<Void> saveRelation(@RequestParam String relation,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateRelation(member, relation);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/gender")
    public ResponseEntity<Void> saveGender(@RequestParam Gender gender,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateGender(member, gender);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/age")
    public ResponseEntity<Void> saveAge(@RequestParam Integer age,
                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateAge(member, age);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/allergy")
    public ResponseEntity<Void> saveAllergy(@RequestParam String allergy,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateAllergy(member, allergy);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/family-history")
    public ResponseEntity<Void> saveFamilyHistory(@RequestParam String familyHistory,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateFamilyHistory(member, familyHistory);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/medication")
    public ResponseEntity<Void> saveMedication(@RequestParam String medication,
                                               @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updateMedication(member, medication);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/past-history")
    public ResponseEntity<Void> savePastHistory(@RequestParam String pastHistory,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.updatePastHistory(member, pastHistory);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/sign")
    public ResponseEntity<Void> saveSign(@RequestParam String sign,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.saveSign(member, sign);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/result")
    public ResponseEntity<MedicationTemplateResponseDTO> getMedicationTemplateResult(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        MedicationTemplateResponseDTO response = medicationTemplateService.requestMedicationTemplateToFastApi(member);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/state")
    public ResponseEntity<MedicationProcessingState> getCurrentState(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        MedicationProcessingState state = medicationTemplateService.getState(member);
        return ResponseEntity.ok(state);
    }

    @DeleteMapping("/state")
    public ResponseEntity<Void> clearState(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        medicationTemplateService.clearState(member);
        return ResponseEntity.ok().build();
    }
}
