package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.SelectedMBPService;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedMBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedMBPResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "main body", description = "주요 신체 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/selected-mbp")
public class SelectedMBPController {
    private final SelectedMBPService selectedMBPService;

    @Operation(summary = "선택한 주요 신체 저장", description = "사용자가 선택한 주요 신체를 저장합니다.")
    @PostMapping
    public ResponseEntity<SelectedMBPResponseDTO> selectMainBodyPart(
            @RequestBody SelectedMBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        SelectedMBPResponseDTO responseDTO = selectedMBPService.saveSelectedMBP(requestDTO, member);
        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "선택한 주요 신체 조회", description = "선택된 주요 신체를 조회합니다.")
    @GetMapping("/{selectedMBPId}")
    public ResponseEntity<SelectedMBPResponseDTO> getSelectedMBP(
            @PathVariable("selectedMBPId") Long selectedMBPId,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        SelectedMBPResponseDTO responseDTO = selectedMBPService.getSelectedMBP(selectedMBPId, member);
        return ResponseEntity.ok(responseDTO);
    }


    @Operation(summary = "선택한 주요 신체 수정", description = "선택된 주요 신체를 수정합니다.")
    @PutMapping("/{selectedMBPId}")
    public ResponseEntity<SelectedMBPResponseDTO> updateSelectedMBP(
            @PathVariable("selectedMBPId") Long selectedMBPId,
            @RequestBody SelectedMBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        SelectedMBPResponseDTO responseDTO = selectedMBPService.updateSelectedMBP(selectedMBPId, requestDTO, member);
        return ResponseEntity.ok(responseDTO);
    }
}
