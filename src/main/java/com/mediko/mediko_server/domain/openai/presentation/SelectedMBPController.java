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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/selected-mbp")
public class SelectedMBPController {
    private final SelectedMBPService selectedMBPService;

    // 선택된 신체 부분 저장
    @PostMapping
    public SelectedMBPResponseDTO selectMainBodyPart(
            @RequestBody SelectedMBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        return selectedMBPService.selectMainBodyPart(requestDTO, member);
    }


    // 선택된 신체 부분 조회
    @GetMapping
    public SelectedMBPResponseDTO getSelectedMBP(
            @AuthenticationPrincipal CustomUserDetails userDetail) {
        Member member = userDetail.getMember();
        return selectedMBPService.getSelectedMBP(member);
    }

    // 선택된 신체 부분 수정
    @PutMapping
    public SelectedMBPResponseDTO updateSelectedMBP(
            @RequestBody SelectedMBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetail) {

        Member member = userDetail.getMember();
        return selectedMBPService.updateSelectedMBP(requestDTO, member);
    }
}
