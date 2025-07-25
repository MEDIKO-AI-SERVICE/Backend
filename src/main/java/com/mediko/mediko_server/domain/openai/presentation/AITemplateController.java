package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.AITemplateService;
import com.mediko.mediko_server.domain.openai.domain.unit.Intensity;
import com.mediko.mediko_server.domain.openai.domain.unit.State;
import com.mediko.mediko_server.domain.openai.domain.unit.TimeUnit;
import com.mediko.mediko_server.domain.openai.dto.request.AdditionalRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSignRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.request.SuggestSignRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.AITemplateListResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.AITemplateResponseDTO;
import com.mediko.mediko_server.global.s3.UuidFileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Tag(name = "ai-template", description = "AI 사전문진 템플릿 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/ai-template")
public class AITemplateController {

    private final AITemplateService aitemplateService;

    // 1. 본인/타인 여부 저장 및 세션ID 발급
    @Operation(summary = "1. 본인/타인 여부 설정", description = "본인/타인 여부를 선택하면 세션 id가 반환됩니다. false인 경우 이후 타인 정보를 입력해야합니다.")
    @PostMapping("/is-self")
    public ResponseEntity<Map<String, String>>  saveIsSelf(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("isSelf") boolean isSelf) {
        Member member = userDetails.getMember();
        String sessionId = aitemplateService.saveIsSelf(member, isSelf);
        return ResponseEntity.ok(Map.of("sessionId", sessionId));
    }

    // 2. 신체 부위 입력 및 증상 후보 조회
    @Operation(summary = "2. 신체 부위 입력 및 증상 후보 조회", description = "신체 부위를 입력하면 증상 후보(형용사) 리스트가 반환됩니다.")
    @PostMapping("/body-part")
    public ResponseEntity<List<String>> saveBodyPart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestBody SuggestSignRequestDTO requestDTO) {
        Member member = userDetails.getMember();
        List<String> adjectives = aitemplateService.saveBodyPart(member, sessionId, requestDTO);
        return ResponseEntity.ok(adjectives);
    }

    // 3. 선택한 증상 저장
    @Operation(summary = "3. 선택한 증상 저장", description = "선택/입력한 증상(들)을 세션에 저장합니다.")
    @PostMapping("/selected-sign")
    public ResponseEntity<Void> saveSelectedSign(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestBody SelectedSignRequestDTO requestDTO) {
        Member member = userDetails.getMember();
        aitemplateService.saveSelectedSign(member, sessionId, requestDTO);
        return ResponseEntity.ok().build();
    }

    // 4. 증상 강도 저장
    @Operation(summary = "4. 증상 강도 저장", description = "증상의 강도를 저장합니다.")
    @PostMapping("/intensity")
    public ResponseEntity<Void> saveIntensity(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("intensity") Intensity intensity) {
        Member member = userDetails.getMember();
        aitemplateService.saveIntensity(member, sessionId, intensity);
        return ResponseEntity.ok().build();
    }

    // 5. 증상 시작일 저장
    @Operation(summary = "5. 증상 시작일 저장", description = "증상이 시작된 날짜를 저장합니다.")
    @PostMapping("/start-date")
    public ResponseEntity<Void> saveStartDate(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("startDate") String startDate) {
        LocalDate date = LocalDate.parse(startDate);
        Member member = userDetails.getMember();
        aitemplateService.saveStartDate(member, sessionId, date);
        return ResponseEntity.ok().build();
    }

    // 6. 증상 지속 기간(값) 저장
    @Operation(summary = "6. 증상 지속 기간(값) 저장", description = "증상이 지속된 기간의 값을 저장합니다.")
    @PostMapping("/duration-value")
    public ResponseEntity<Void> saveDurationValue(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("durationValue") Integer durationValue) {
        Member member = userDetails.getMember();
        aitemplateService.saveDurationValue(member, sessionId, durationValue);
        return ResponseEntity.ok().build();
    }

    // 7. 증상 지속 기간(단위) 저장
    @Operation(summary = "7. 증상 지속 기간(단위) 저장", description = "증상이 지속된 기간의 단위를 저장합니다.")
    @PostMapping("/duration-unit")
    public ResponseEntity<Void> saveDurationUnit(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("durationUnit") TimeUnit durationUnit) {
        Member member = userDetails.getMember();
        aitemplateService.saveDurationUnit(member, sessionId, durationUnit);
        return ResponseEntity.ok().build();
    }


    // 8. 증상 경과 저장
    @Operation(summary = "8. 증상 경과 저장", description = "증상이 경과된 상태를 저장합니다.")
    @PostMapping("/state")
    public ResponseEntity<Void> saveState(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("state") State state) {
        Member member = userDetails.getMember();
        aitemplateService.saveState(member, sessionId, state);
        return ResponseEntity.ok().build();
    }


    // 9. 추가 정보 저장
    @Operation(summary = "9. 추가 정보 저장",
               description = "hasAdditional=true면 body에 추가 정보를 입력, false면 추가 정보를 입력하지 않습니다.")
    @PostMapping("/additional")
    public ResponseEntity<Void> saveAdditional(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("hasAdditional") boolean hasAdditional,
            @RequestBody(required = false) AdditionalRequestDTO requestDTO) {
        Member member = userDetails.getMember();
        aitemplateService.saveAdditional(member, sessionId, hasAdditional, requestDTO);
        return ResponseEntity.ok().build();
    }


    // 10. 증상 관련 이미지 업로드
    @Operation(summary = "10. 증상 이미지 첨부 및 결과 반환",
               description = "hasImages=true면 part에 이미지를 첨부, false면 이미지를 첨부하지 않고 결과를 반환합니다")
    @PostMapping(value ="/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> uploadImages(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("sessionId") String sessionId,
            @RequestParam("hasImages") boolean hasImages,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        Member member = userDetails.getMember();
        AITemplateResponseDTO dto = aitemplateService.uploadImagesAndReturnResult(
                sessionId, files, hasImages, member
        );
        Map<String, Object> result = Map.of(
                "summary", dto.toSummaryMap(),
                "analysis", dto.toAnalysisMap()
        );
        return ResponseEntity.ok(result);
    }


    // 11. 사전문진 분석 결과 조회
    @Operation(summary = "특정 사전문진 결과 조회 (분석용)", description = "사전문진 분석 결과를 조회합니다.")
    @GetMapping("/result/analysis/{aiId}")
    public ResponseEntity<Map<String, Object>> getAnalysisResult(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("aiId") Long aiId) {
        Member member = userDetails.getMember();
        Map<String, Object> result = aitemplateService.getResultByAiId(aiId, member);
        return ResponseEntity.ok((Map<String, Object>) result.get("analysis"));
    }

    // 12. 사전문진 요약 결과 조회
    @Operation(summary = "특정 사전문진 결과 조회 (요약용)", description = "사전문진 요약 결과를 조회합니다.")
    @GetMapping("/result/summary/{aiId}")
    public ResponseEntity<Map<String, Object>> getSummaryResult(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("aiId") Long aiId) {
        Member member = userDetails.getMember();
        Map<String, Object> result = aitemplateService.getResultByAiId(aiId, member);
        return ResponseEntity.ok((Map<String, Object>) result.get("summary"));
    }

    // 13. ai_id로 사전문진 결과 조회
    @Operation(summary = "특정 사전문진 결과 조회", description = "ai_id를 입력하면 해당 사전문진의 summary와 analysis 결과를 조회합니다.")
    @GetMapping("/result/{aiId}")
    public ResponseEntity<Map<String, Object>> getResultByAiId(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable("aiId") Long aiId) {
        Member member = userDetails.getMember();
        Map<String, Object> result = aitemplateService.getResultByAiId(aiId, member);
        return ResponseEntity.ok(result);
    }

    // 14. 사용자의 모든 사전문진 조회 (최신순)
    @Operation(summary = "리스트용 제목 조회 (모두)", description = "사용자가 생성한 모든 사전문진의 리스트용 제목을 최신순으로 조회합니다.")
    @GetMapping("/results/all")
    public ResponseEntity<List<AITemplateListResponseDTO>> getAllUserResults(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        List<AITemplateListResponseDTO> results = aitemplateService.getAllUserResults(member);
        return ResponseEntity.ok(results);
    }

    // 15. 사용자의 최신 사전문진 3개 조회
    @Operation(summary = "리스트용 제목 조회 (최신 3개)", description = "사용자가 생성한 사전문진의 리스트용 제목을 최신 3개만 조회합니다.")
    @GetMapping("/results/latest")
    public ResponseEntity<List<AITemplateListResponseDTO>> getLatestThreeResults(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        List<AITemplateListResponseDTO> results = aitemplateService.getLatestThreeResults(member);
        return ResponseEntity.ok(results);
    }


//    // 상태 조회 (선택)
//    @GetMapping("/state")
//    public ResponseEntity<?> getState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        return ResponseEntity.ok(aitemplateService.getState(member, sessionId));
//    }
//
//    // 상태 삭제 (선택)
//    @DeleteMapping("/state")
//    public ResponseEntity<Void> clearState(
//            @RequestParam("sessionId") String sessionId,
//            @AuthenticationPrincipal CustomUserDetails userDetails) {
//        Member member = userDetails.getMember();
//        aitemplateService.clearState(member, sessionId);
//        return ResponseEntity.ok().build();
//    }
}
