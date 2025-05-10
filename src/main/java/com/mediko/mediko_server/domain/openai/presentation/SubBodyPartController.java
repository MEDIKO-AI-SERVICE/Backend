package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.SubBodyPartService;
import com.mediko.mediko_server.domain.openai.dto.response.SubBodyPartResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Tag(name = "sub body", description = "세부 신체 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/sub-body")
public class SubBodyPartController {
    private final SubBodyPartService subBodyPartService;

    @Operation(summary = "세부 신체 전체 조회", description = "모든 세부 신체를 조회합니다.")
    @GetMapping("/all")
    public ResponseEntity<List<SubBodyPartResponseDTO>> findAll(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        List<SubBodyPartResponseDTO> subBodyParts = subBodyPartService.findAll(member);
        return ResponseEntity.ok(subBodyParts);
    }

    @GetMapping
    public ResponseEntity<Map<String, List<SubBodyPartResponseDTO>>> getSubBodyPartsByBodies(
            @RequestParam("body") List<String> body,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        Map<String, List<SubBodyPartResponseDTO>> response =
                subBodyPartService.getSubBodyPartsByMainBodyPartBodies(body, member);
        return ResponseEntity.ok(response);
    }
}
