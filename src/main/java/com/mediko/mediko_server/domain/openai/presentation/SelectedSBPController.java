package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.SelectedSBPService;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedSBPResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/selected-sbp")
public class SelectedSBPController {
    private final SelectedSBPService selectedSBPService;

    //선택된 세부신체 부분 저장
    @PostMapping("/{selectedMBPId}")
    public ResponseEntity<SelectedSBPResponseDTO> saveSelectedSBP(
            @PathVariable("selectedMBPId") Long selectedMBPId,
            @RequestBody SelectedSBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetail
    ) {
        Member member = userDetail.getMember();
        SelectedSBPResponseDTO responseDTO = selectedSBPService.saveSelectedSBP(member, requestDTO, selectedMBPId);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }


    // 특정 세부신체 부분 조회
    @GetMapping("/{selectedSBPId}")
    public ResponseEntity<SelectedSBPResponseDTO> getSelectedSBP(
            @PathVariable("selectedSBPId") Long selectedSBPId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        SelectedSBPResponseDTO responseDTO = selectedSBPService.getSelectedSBP(selectedSBPId, member);
        return ResponseEntity.ok(responseDTO);
    }

    // 특정 세부신체 부분 수정
    @PutMapping("/{selectedSBPId}")
    public ResponseEntity<SelectedSBPResponseDTO> updateSelectedSBP(
            @PathVariable("selectedSBPId") Long selectedSBPId,
            @RequestBody SelectedSBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        SelectedSBPResponseDTO responseDTO = selectedSBPService.updateSelectedSBP(selectedSBPId, member, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

}
