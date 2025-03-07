package com.mediko.mediko_server.domain.map.presentation;

import com.mediko.mediko_server.domain.map.application.SelectedErService;
import com.mediko.mediko_server.domain.map.dto.response.ErWithMapUrlDTO;
import com.mediko.mediko_server.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "er", description = "응급실 API")
@RestController
@RequestMapping("/api/v1/er-map")
@RequiredArgsConstructor
public class SelectedErController {

    private final SelectedErService selectedErService;

    @Operation(summary = "응급실 선택 및 map url", description = "사용자가 선택한 응급실을 저장하고 map url을 반환합니다.")
    @GetMapping("/{erId}")
    public ResponseEntity<ErWithMapUrlDTO> getErWithMapUrls(
            @PathVariable Long erId,
            @AuthenticationPrincipal Member member) {
        ErWithMapUrlDTO response = selectedErService.getErWithMapUrls(erId, member);
        return ResponseEntity.ok(response);
    }
}
