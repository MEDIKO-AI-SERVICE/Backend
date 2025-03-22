package com.mediko.mediko_server.domain.openai.presentation;


import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.member.domain.infoType.Language;
import com.mediko.mediko_server.domain.openai.application.MainBodyPartService;
import com.mediko.mediko_server.domain.openai.dto.response.MainBodyPartResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "main body", description = "주요 신체 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/main-body")
public class MainBodyPartController {
    private final MainBodyPartService mainBodyPartService;

    @Operation(summary = "주요 신체 전체 조회", description = "모든 주요 신체를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<MainBodyPartResponseDTO>> findAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        List<MainBodyPartResponseDTO> mainBodyParts = mainBodyPartService.findAll(member);
        return ResponseEntity.ok(mainBodyParts);
    }
}
