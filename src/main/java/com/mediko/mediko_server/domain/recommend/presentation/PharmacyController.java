package com.mediko.mediko_server.domain.recommend.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.application.PharmacyService;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequest_1DTO;
import com.mediko.mediko_server.domain.recommend.dto.request.PharmacyRequest_2DTO;
import com.mediko.mediko_server.domain.recommend.dto.response.PharmacyResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "pharmacy", description = "약국 추천 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/pharmacy")
public class PharmacyController {
    private final PharmacyService pharmacyService;

    // 간단 위치 기반 약국 추천 (PharmacyRequest_1DTO, sortType은 RECOMMEND로 고정)
    @Operation(summary = "약국 추천 (간단)", description = "위치 정보만으로 약국 추천 리스트를 반환합니다.")
    @PostMapping("/template")
    public ResponseEntity<List<PharmacyResponseDTO>> recommendPharmacySimple(
            @RequestBody PharmacyRequest_1DTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        List<PharmacyResponseDTO> responseDTOList = pharmacyService.recommendPharmacy(requestDTO, member);

        return ResponseEntity.ok(responseDTOList);
    }

    // 상세 옵션 기반 약국 추천 (PharmacyRequest_2DTO)
    @Operation(summary = "약국 추천 (상세)", description = "사용자 옵션 기반 약국 추천 리스트를 반환합니다.")
    @PostMapping("/manual")
    public ResponseEntity<List<PharmacyResponseDTO>> recommendPharmacy(
            @RequestBody PharmacyRequest_2DTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        List<PharmacyResponseDTO> responseDTOList = pharmacyService.recommendPharmacy(requestDTO, member);

        return ResponseEntity.ok(responseDTOList);
    }
}
