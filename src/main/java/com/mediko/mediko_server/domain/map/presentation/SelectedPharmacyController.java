package com.mediko.mediko_server.domain.map.presentation;

import com.mediko.mediko_server.domain.map.application.SelectedPharmacyService;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.map.dto.response.SelectedPharmacyResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/selecte-ph")
@RequiredArgsConstructor
public class SelectedPharmacyController {
    private final SelectedPharmacyService selectedPharmacyService;

    @Value("${app.name}")
    private String appName;

    //약국 선택 및 저장
    @PostMapping("/{pharmacyId}")
    public ResponseEntity<SelectedPharmacyResponseDTO> saveSelectedPharmacy(
            @PathVariable Long pharmacyId,
            @AuthenticationPrincipal Member member
    ) {
        SelectedPharmacyResponseDTO response = selectedPharmacyService
                .saveSelectedPharmacy(pharmacyId, member, appName);
        return ResponseEntity.ok(response);
    }



    //약국의 지도 URL 조회
    @GetMapping("/{pharmacyId}/maps")
    public ResponseEntity<MapUrlResponseDTO> getMapUrlsForPharmacy(
            @PathVariable Long pharmacyId
    ) {
        MapUrlResponseDTO response = selectedPharmacyService.getMapUrlsForPharmacy(pharmacyId);
        return ResponseEntity.ok(response);
    }
}
