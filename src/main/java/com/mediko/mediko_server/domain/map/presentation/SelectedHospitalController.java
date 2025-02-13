package com.mediko.mediko_server.domain.map.presentation;


import com.mediko.mediko_server.domain.map.application.SelectedHospitalService;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.map.dto.response.SelectedHospitalResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/selecte-hp")
@RequiredArgsConstructor
public class SelectedHospitalController {
    private final SelectedHospitalService selectedHospitalService;

    @Value("${app.name}")
    private String appName;

    //병원 선택 및 저장
    @PostMapping("/{hospitalId}")
    public ResponseEntity<SelectedHospitalResponseDTO> saveSelectedHospital(
            @PathVariable Long hospitalId,
            @AuthenticationPrincipal Member member
    ) {
        SelectedHospitalResponseDTO response = selectedHospitalService
                .saveSelectedHospital(hospitalId, member, appName);
        return ResponseEntity.ok(response);
    }




    //특정 병원의 지도 URL 조회
    @GetMapping("/{hospitalId}/maps")
    public ResponseEntity<MapUrlResponseDTO> getMapUrlsForHospital(
            @PathVariable Long hospitalId
    ) {
        MapUrlResponseDTO response = selectedHospitalService.getMapUrlsForHospital(hospitalId);
        return ResponseEntity.ok(response);
    }
}
