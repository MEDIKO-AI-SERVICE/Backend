package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.SelectedMBPService;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedMBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedMBPResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/selected-mbp")
public class SelectedMBPController {
    private final SelectedMBPService selectedMBPService;

    // 선택된 주신체 부분 저장
    @PostMapping
    public ResponseEntity<SelectedMBPResponseDTO> selectMainBodyPart(
            @RequestBody SelectedMBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        SelectedMBPResponseDTO responseDTO = selectedMBPService.saveSelectedMBP(requestDTO, member);
        return ResponseEntity.ok(responseDTO);
    }

    // 최신 주신체 부분 조회
    @GetMapping
    public ResponseEntity<SelectedMBPResponseDTO> getSelectedMBP(
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        SelectedMBPResponseDTO responseDTO = selectedMBPService.getLatestSelectedSBP(member);
        return ResponseEntity.ok(responseDTO);
    }

    // 최신 주신체 부분 수정
    @PutMapping
    public ResponseEntity<SelectedMBPResponseDTO> updateSelectedMBP(
            @RequestBody SelectedMBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        SelectedMBPResponseDTO responseDTO = selectedMBPService.updateLatestSelectedSBP(requestDTO, member);
        return ResponseEntity.ok(responseDTO);
    }

}
