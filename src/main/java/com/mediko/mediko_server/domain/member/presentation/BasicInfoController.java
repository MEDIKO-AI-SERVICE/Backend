package com.mediko.mediko_server.domain.member.presentation;

import com.mediko.mediko_server.domain.member.application.BasicInfoService;
import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.HealthInfoResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/basicInfo")
public class BasicInfoController {

    private final BasicInfoService basicInfoService;

    // BasicInfo 저장
    @PostMapping
    public ResponseEntity<BasicInfoResponseDTO> saveBasicInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody BasicInfoRequestDTO basicInfoRequestDTO) {
        String loginId = customUserDetails.getUsername();
        BasicInfoResponseDTO savedBasicInfo = basicInfoService.saveBasicInfo(loginId, basicInfoRequestDTO);
        return ResponseEntity.status(CREATED).body(savedBasicInfo);
    }

    // BasicInfo 조회
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BasicInfoResponseDTO> getBasicInfo(
            @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        String loginId = customUserDetails.getUsername();
        BasicInfoResponseDTO basicInfoResponseDTO = basicInfoService.getBasicInfo(loginId);
        return ResponseEntity.ok(basicInfoResponseDTO);
    }

    // BasicInfo 수정
    @PatchMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BasicInfoResponseDTO> updateBasicInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BasicInfoRequestDTO basicInfoRequestDTO) {
        String loginId = userDetails.getUsername();
        BasicInfoResponseDTO updatedBasicInfo = basicInfoService.updateBasicInfo(loginId, basicInfoRequestDTO);
        return ResponseEntity.ok(updatedBasicInfo);
    }
}
