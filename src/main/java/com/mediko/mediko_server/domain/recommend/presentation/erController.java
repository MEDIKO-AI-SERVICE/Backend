package com.mediko.mediko_server.domain.recommend.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.recommend.application.ErService;
import com.mediko.mediko_server.domain.recommend.dto.request.ErRequestDTO;
import com.mediko.mediko_server.domain.recommend.dto.response.ErResponseDTO;
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

@Tag(name = "er", description = "응급실 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/er")
public class erController {
    private final ErService erService;

    @Operation(summary = "응급실 추천", description = "응급실 추천 리스트를 반환합니다.")
    @PostMapping
    public ResponseEntity<List<ErResponseDTO>> recommendEr(
            @RequestBody ErRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        List<ErResponseDTO> responseDTOList = erService.recommendEr(requestDTO, member);

        return ResponseEntity.ok(responseDTOList);
    }
}
