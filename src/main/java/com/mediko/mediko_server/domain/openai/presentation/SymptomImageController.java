package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.SymptomImageService;
import com.mediko.mediko_server.global.s3.UuidFileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "symptom", description = "증상 강도 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/symptom/img")
public class SymptomImageController {
    private final SymptomImageService symptomImageService;

    @Operation(summary = "증상 이미지 업로드", description = "사용자의 증상 이미지를 업로드합니다.")
    @PostMapping(value = "/{symptomId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<UuidFileResponseDTO>> uploadImages(
            @PathVariable("symptomId") Long symptomId,
            @RequestParam("file") List<MultipartFile> files,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(symptomImageService.uploadImages(symptomId, files, member));
    }

    @Operation(summary = "증상 이미지 조회", description = "사용자의 증상 이미지를 조회합니다.")
    @GetMapping("/{symptomId}")
    public ResponseEntity<List<UuidFileResponseDTO>> getImages(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(symptomImageService.getSymptomImages(symptomId, member));
    }

    @Operation(summary = "증상 이미지 개별 삭제", description = "사용자의 증상 이미지를 개별적으로 삭제합니다.")
    @DeleteMapping("/{symptomId}/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable("symptomId") Long symptomId,
            @PathVariable("imageId") Long imageId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        symptomImageService.deleteImage(symptomId, imageId, member);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "증상 이미지 전체 삭제", description = "사용자의 증상 이미지들을 일괄적으로 삭제합니다.")
    @DeleteMapping("/{symptomId}")
    public ResponseEntity<Void> deleteAllImages(
            @PathVariable("symptomId") Long symptomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        symptomImageService.deleteAllImages(symptomId, member);
        return ResponseEntity.noContent().build();
    }
}
