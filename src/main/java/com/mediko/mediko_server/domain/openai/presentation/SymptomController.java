package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.SymptomService;
import com.mediko.mediko_server.domain.openai.dto.request.AdditionalInfoRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.DurationRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.IntensityRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.PainStartRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.AdditionalInfoResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.DurationResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.IntensityResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.PainStartResponseDTO;
import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/symptom")
public class SymptomController {

    private final SymptomService symptomService;

    /**
     * PainStart 관련 메서드
     */
    @PostMapping("/start")
    public ResponseEntity<PainStartResponseDTO> savePainStart(
            @RequestBody PainStartRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        PainStartResponseDTO response = symptomService.savePainStart(requestDTO, member);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/start/{symptomId}")
    public ResponseEntity<PainStartResponseDTO> getPainStart(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        PainStartResponseDTO response = symptomService.getPainStart(symptomId, member);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/start/{symptomId}")
    public ResponseEntity<PainStartResponseDTO> updatePainStart(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody PainStartRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        PainStartResponseDTO response = symptomService.updatePainStart(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }


    /**
     * Intensity 관련 메서드
     */
    @PutMapping("/intensity/{symptomId}")
    public ResponseEntity<IntensityResponseDTO> updateIntensity(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody IntensityRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        IntensityResponseDTO response = symptomService.updateIntensity(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/intensity/{symptomId}")
    public ResponseEntity<IntensityResponseDTO> getIntensity(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        IntensityResponseDTO response = symptomService.getIntensity(symptomId, member);
        return ResponseEntity.ok(response);
    }

    /**
     * Duration 관련 메서드
     */
    @PutMapping("/duration/{symptomId}")
    public ResponseEntity<DurationResponseDTO> updateDuration(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody DurationRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        DurationResponseDTO response = symptomService.updateDuration(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/duration/{symptomId}")
    public ResponseEntity<DurationResponseDTO> getDuration(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        DurationResponseDTO response = symptomService.getDuration(symptomId, member);
        return ResponseEntity.ok(response);
    }


    /**
     * AdditionalInfo 관련 메서드
     */
    @PutMapping("/additional/{symptomId}")
    public ResponseEntity<AdditionalInfoResponseDTO> updateAdditionalInfo(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody AdditionalInfoRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        AdditionalInfoResponseDTO response = symptomService.updateAdditionalInfo(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/additional/{symptomId}")
    public ResponseEntity<AdditionalInfoResponseDTO> getAdditionalInfo(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        AdditionalInfoResponseDTO response = symptomService.getAdditionalInfo(symptomId, member);
        return ResponseEntity.ok(response);
    }
}
