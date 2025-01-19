package com.mediko.mediko_server.domain.member.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.application.HealthInfoService;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.dto.request.HealthInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.HealthInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/healthInfo")
public class HealthInfoController {

    private final HealthInfoService healthInfoService;

    // HealthInfo 저장
    @PostMapping
    public ResponseEntity<HealthInfoResponseDTO> saveHealthInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody HealthInfoRequestDTO healthInfoRequestDTO) {
        Member member = userDetails.getMember();
        HealthInfoResponseDTO savedHealthInfo = healthInfoService.saveHealthInfo(member, healthInfoRequestDTO);
        return ResponseEntity.status(CREATED).body(savedHealthInfo);
    }

    // HealthInfo 조회
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<HealthInfoResponseDTO> getHealthInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        HealthInfoResponseDTO healthInfoResponseDTO = healthInfoService.getHealthInfo(member);
        return ResponseEntity.ok(healthInfoResponseDTO);
    }

    // HealthInfo 수정
    @PatchMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<HealthInfoResponseDTO> updateHealthInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody HealthInfoRequestDTO healthInfoRequestDTO) {
        Member member = userDetails.getMember();
        HealthInfoResponseDTO updatedHealthInfo = healthInfoService.updateHealthInfo(member, healthInfoRequestDTO);
        return ResponseEntity.ok(updatedHealthInfo);
    }
}
