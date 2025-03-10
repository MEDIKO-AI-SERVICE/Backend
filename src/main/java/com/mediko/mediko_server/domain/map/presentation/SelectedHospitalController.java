package com.mediko.mediko_server.domain.map.presentation;

import com.mediko.mediko_server.domain.map.application.SelectedHospitalService;
import com.mediko.mediko_server.domain.map.dto.response.HospitalWithMapUrlDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "hospital", description = "병원 추천 API")
@RestController
@RequestMapping("/api/v1/hospital-map")
@RequiredArgsConstructor
public class SelectedHospitalController {
    private final SelectedHospitalService selectedHospitalService;

    @Operation(summary = "병원 선택 및 map url", description = "사용자가 선택한 병원을 저장하고 map url을 반환합니다.")
    @GetMapping("/{hospitalId}")
    public ResponseEntity<HospitalWithMapUrlDTO> getHospitalWithMapUrls(
            @PathVariable("hospitalId") Long hospitalId,
            @AuthenticationPrincipal Member member) {
        HospitalWithMapUrlDTO response = selectedHospitalService.getHospitalWithMapUrls(hospitalId, member);
        return ResponseEntity.ok(response);
    }
}
