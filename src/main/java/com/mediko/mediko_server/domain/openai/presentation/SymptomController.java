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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "symptom", description = "증상 강도 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/symptom")
public class SymptomController {

    private final SymptomService symptomService;

    /**
     * PainStart 관련 메서드
     */
    @Operation(summary = "증상 시작시간 저장", description = "증상이 시작된 시간을 저장합니다.")
    @PostMapping("/start/{selectedSignIds}")
    public ResponseEntity<PainStartResponseDTO> savePainStart(
            @PathVariable("selectedSignIds") String selectedSignIds,
            @RequestBody PainStartRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String decodedSignIds = URLDecoder.decode(selectedSignIds, StandardCharsets.UTF_8);

        // 콤마가 없는 경우도 처리
        List<Long> idList;
        if (decodedSignIds.contains(",")) {
            idList = Arrays.stream(decodedSignIds.split(","))
                    .map(Long::parseLong)
                    .collect(Collectors.toList());
        } else {
            idList = Collections.singletonList(Long.parseLong(decodedSignIds));
        }

        Member member = userDetails.getMember();
        PainStartResponseDTO response = symptomService.savePainStart(requestDTO, idList, member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "증상 시작시간 조회", description = "저장된 증상 시작시간을 조회합니다.")
    @GetMapping("/start/{symptomId}")
    public ResponseEntity<PainStartResponseDTO> getPainStart(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        PainStartResponseDTO response = symptomService.getPainStart(symptomId, member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "증상 시작시간 수정", description = "저장된 증상 시작시간을 수정합니다.")
    @PutMapping("/start/{symptomId}")
    public ResponseEntity<PainStartResponseDTO> updatePainStart(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody PainStartRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        PainStartResponseDTO response = symptomService.updatePainStart(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }


    /**
     * Intensity 관련 메서드
     */
    @Operation(summary = "증상 강도 저장", description = "증상의 강도를 저장합니다.")
    @PostMapping("/intensity/{symptomId}")
    public ResponseEntity<IntensityResponseDTO> saveIntensity(
            @PathVariable("symptomId") Long symptomId,
            @Valid @RequestBody IntensityRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        IntensityResponseDTO response = symptomService.saveIntensity(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }


    @Operation(summary = "증상 강도 조회", description = "저장된 증상의 강도를 조회합니다.")
    @GetMapping("/intensity/{symptomId}")
    public ResponseEntity<IntensityResponseDTO> getIntensity(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        IntensityResponseDTO response = symptomService.getIntensity(symptomId, member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "증상 강도 수정", description = "저장된 중상의 강도를 수정합니다.")
    @PutMapping("/intensity/{symptomId}")
    public ResponseEntity<IntensityResponseDTO> updateIntensity(
            @PathVariable("symptomId") Long symptomId,
            @Valid @RequestBody IntensityRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        IntensityResponseDTO response = symptomService.updateIntensity(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }

    /**
     * Duration 관련 메서드
     */
    @Operation(summary = "증상 지속기간 저장", description = "증상의 지속기간을 저장힙니다.")
    @PostMapping("/duration/{symptomId}")
    public ResponseEntity<DurationResponseDTO> saveDuration(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody DurationRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        DurationResponseDTO response = symptomService.saveDuration(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "증상 지속기간 조회", description = "저장된 증상의 지속기간을 조회합니다.")
    @GetMapping("/duration/{symptomId}")
    public ResponseEntity<DurationResponseDTO> getDuration(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        DurationResponseDTO response = symptomService.getDuration(symptomId, member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "증상 지속기간 수정", description = "저장된 증상의 지속기간을 수정합니다.")
    @PutMapping("/duration/{symptomId}")
    public ResponseEntity<DurationResponseDTO> updateDuration(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody DurationRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        DurationResponseDTO response = symptomService.updateDuration(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }


    /**
     * AdditionalInfo 관련 메서드
     */
    @Operation(summary = "증상 추가정보 저장", description = "증상의 추가정보를 저장합니다.")
    @PostMapping("/additional/{symptomId}")
    public ResponseEntity<AdditionalInfoResponseDTO> saveAdditionalInfo(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody AdditionalInfoRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        AdditionalInfoResponseDTO response = symptomService.saveAdditionalInfo(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "증상 추가정보 조회", description = "저장된 증상의 추가정보를 조회합니다.")
    @GetMapping("/additional/{symptomId}")
    public ResponseEntity<AdditionalInfoResponseDTO> getAdditionalInfo(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        AdditionalInfoResponseDTO response = symptomService.getAdditionalInfo(symptomId, member);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "증상 추가정보 수정", description = "증상의 추가정보를 수정합니다.")
    @PutMapping("/additional/{symptomId}")
    public ResponseEntity<AdditionalInfoResponseDTO> updateAdditionalInfo(
            @PathVariable("symptomId") Long symptomId,
            @RequestBody AdditionalInfoRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        AdditionalInfoResponseDTO response = symptomService.updateAdditionalInfo(symptomId, requestDTO, member);
        return ResponseEntity.ok(response);
    }
}
