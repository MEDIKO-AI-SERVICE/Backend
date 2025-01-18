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

    // 특정 selectedMBP 조회
    @GetMapping("/{selectedMBPId}")
    public ResponseEntity<SelectedMBPResponseDTO> getSelectedMBP(
            @PathVariable("selectedMBPId") Long selectedMBPId,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        SelectedMBPResponseDTO responseDTO = selectedMBPService.getSelectedMBP(selectedMBPId, member);
        return ResponseEntity.ok(responseDTO);
    }


    // 특정 주신체 부분 수정
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
