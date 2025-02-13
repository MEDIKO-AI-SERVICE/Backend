package com.mediko.mediko_server.domain.map.presentation;

import com.mediko.mediko_server.domain.map.application.SelectedErService;
import com.mediko.mediko_server.domain.map.dto.response.MapUrlResponseDTO;
import com.mediko.mediko_server.domain.map.dto.response.SelectedErResponseDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/selecte-er")
@RequiredArgsConstructor
public class SelecedErController {
    /*
    private final SelectedErService selectedErService;

    @Value("${app.name}")
    private String appName;

    //응급실 선택 및 저장
    @PostMapping("/{erId}")
    public ResponseEntity<SelectedErResponseDTO> saveSelectedEr(
            @PathVariable Long erId,
            @AuthenticationPrincipal Member member
    ) {
        SelectedErResponseDTO response = selectedErService
                .saveSelectedEr(erId, member, appName);
        return ResponseEntity.ok(response);
    }




    //응급실 병원의 지도 URL 조회
    @GetMapping("/{erId}/maps")
    public ResponseEntity<MapUrlResponseDTO> getMapUrlsForEr(
            @PathVariable Long erId
    ) {
        MapUrlResponseDTO response = selectedErService.getMapUrlsForEr(erId);
        return ResponseEntity.ok(response);
    }
    */
}
