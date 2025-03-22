package com.mediko.mediko_server.domain.member.presentation;

import com.mediko.mediko_server.domain.member.application.BasicInfoService;
import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.dto.request.BasicInfoRequestDTO;
import com.mediko.mediko_server.domain.member.dto.request.LanguageRequestDTO;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.ErPasswordResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.LanguageResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;

@Tag(name = "basic info", description = "사용자 기본 정보 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/basicInfo")
public class BasicInfoController {

    private final BasicInfoService basicInfoService;

    @Operation(summary = "사용자 언어 설정", description = "사용자의 언어를 설정합니다.")
    @PostMapping("/language")
    public ResponseEntity<LanguageResponseDTO> setLanguage(
            @RequestParam("memberId") Long memberId,
            @RequestBody LanguageRequestDTO languageRequestDTO) {

        LanguageResponseDTO responseDTO = basicInfoService.setLanguage(memberId, languageRequestDTO);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "사용자 언어 변경", description = "사용자의 언어를 변경합니다.")
    @PatchMapping("/language")
    public ResponseEntity<LanguageResponseDTO> updateLanguage(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody LanguageRequestDTO languageRequestDTO) {

        Member member = customUserDetails.getMember();
        LanguageResponseDTO responseDTO = basicInfoService.updateLanguage(member, languageRequestDTO);

        return ResponseEntity.ok(responseDTO);
    }


    @Operation(summary = "사용자 기본 정보 생성", description = "사용자의 기본 정보를 최초 생성합니다.")
    @PostMapping
    public ResponseEntity<BasicInfoResponseDTO> saveBasicInfo(
            @RequestParam("memberId") Long memberId,
            @RequestBody BasicInfoRequestDTO requestDTO) {
        return ResponseEntity
                .status(CREATED)
                .body(basicInfoService.saveBasicInfo(memberId, requestDTO));
    }

    @Operation(summary = "사용자 기본 정보 수정", description = "저장된 사용자의 기본 정보를 부분 수정합니다.")
    @PatchMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BasicInfoResponseDTO> updateBasicInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody BasicInfoRequestDTO requestDTO) {

        Member member = userDetails.getMember();
        BasicInfoResponseDTO updatedBasicInfo = basicInfoService.updateBasicInfo(member, requestDTO);

        return ResponseEntity.ok(updatedBasicInfo);
    }

    @Operation(summary = "사용자 기본 정보 조회", description = "저장된 사용자의 기본 정보를 조회합니다.")
    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<BasicInfoResponseDTO> getBasicInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(basicInfoService.getBasicInfo(member));
    }

    @Operation(summary = "사용자 응급 비밀번호 조회", description = "저장된 사용자의 응급 비밀번호를 조회합니다.")
    @GetMapping("/er-pw")
    public ResponseEntity<ErPasswordResponseDTO> getErPassword(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        return ResponseEntity.ok(basicInfoService.getErPassword(member));
    }
}
