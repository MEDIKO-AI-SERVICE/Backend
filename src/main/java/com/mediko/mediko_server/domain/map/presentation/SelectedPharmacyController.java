package com.mediko.mediko_server.domain.map.presentation;

import com.mediko.mediko_server.domain.map.application.SelectedPharmacyService;
import com.mediko.mediko_server.domain.map.dto.response.PharmacyWithMapUrlDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "pharmacy", description = "약국 추천 API")
@RestController
@RequestMapping("/api/v1/pharmacy-map")
@RequiredArgsConstructor
public class SelectedPharmacyController {
    private final SelectedPharmacyService selectedPharmacyService;

    @Operation(summary = "약국 선택 및 map url", description = "사용자가 선택한 약국을 저장하고 map url을 반환합니다.")
    @GetMapping("/{pharmacyId}")
    public ResponseEntity<PharmacyWithMapUrlDTO> getPharmacyWithMapUrls(
            @PathVariable Long pharmacyId,
            @AuthenticationPrincipal Member member) {
        PharmacyWithMapUrlDTO response = selectedPharmacyService.getPharmacyWithMapUrls(pharmacyId, member);
        return ResponseEntity.ok(response);
    }
}
