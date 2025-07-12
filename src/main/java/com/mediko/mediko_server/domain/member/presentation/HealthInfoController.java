package com.mediko.mediko_server.domain.member.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.application.HealthInfoService;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.dto.request.HealthInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.HealthInfoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "health info", description = "사용자 건강 정보 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/healthInfo")
public class HealthInfoController {

    private final HealthInfoService healthInfoService;

    @Operation(summary = "회원4. 사용자 건강 정보 저장", description = "회원가입 후 사용자의 건강 정보를 저장합니다.")
    @PostMapping
    public ResponseEntity<HealthInfoResponseDTO> saveHealthInfo(
            @RequestParam("memberId") Long memberId,
            @RequestBody HealthInfoRequestDTO healthInfoRequestDTO) {
        HealthInfoResponseDTO savedHealthInfo = healthInfoService.saveHealthInfo(memberId, healthInfoRequestDTO);
        return ResponseEntity.status(CREATED).body(savedHealthInfo);
    }

    @Operation(summary = "사용자 건강 정보 조회", description = "저장된 사용자의 건강 정보를 조회합니다.")
    @GetMapping
    public ResponseEntity<HealthInfoResponseDTO> getHealthInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        HealthInfoResponseDTO healthInfoResponseDTO = healthInfoService.getHealthInfo(member);
        return ResponseEntity.ok(healthInfoResponseDTO);
    }

    @Operation(summary = "사용자 건강 정보 수정", description = "저장된 사용자의 건강 정보를 수정합니다.")
    @PatchMapping
    public ResponseEntity<HealthInfoResponseDTO> updateHealthInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody HealthInfoRequestDTO healthInfoRequestDTO) {
        Member member = userDetails.getMember();
        HealthInfoResponseDTO updatedHealthInfo = healthInfoService.updateHealthInfo(member, healthInfoRequestDTO);
        return ResponseEntity.ok(updatedHealthInfo);
    }

}
