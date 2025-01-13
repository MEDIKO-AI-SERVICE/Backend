package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.SelectedSBPService;
import com.mediko.mediko_server.domain.openai.domain.SubBodyPart;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSBPRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedSBPResponseDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SubBodyPartResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/selected-sbp")
public class SelectedSBPController {
    private final SelectedSBPService selectedSBPService;

    //선택된 주신체 부분에 포함된 모든 세부신체 부분 조회
    @PostMapping
    public ResponseEntity<List<SubBodyPartResponseDTO>> getSubBodyPartsByBodies(
            @RequestBody SelectedSBPRequestDTO requestBody
    ) {
        List<SubBodyPart> subBodyParts = selectedSBPService.getSubBodyPartsByMainBodyPartBodies(requestBody.getBody());
        List<SubBodyPartResponseDTO> response = subBodyParts.stream()
                .map(SubBodyPartResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    //선택된 세부신체 부분 저장
    @PostMapping("/save")
    public ResponseEntity<SelectedSBPResponseDTO> saveSelectedSBP(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestBody SelectedSBPRequestDTO requestDTO
    ) {
        Member member = userDetail.getMember();
        SelectedSBPResponseDTO responseDTO = selectedSBPService.saveSelectedSBP(member, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    // 최신 세부신체 부분 조회
    @GetMapping
    public ResponseEntity<SelectedSBPResponseDTO> getLatestSelectedSBP(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Member member = userDetails.getMember();
        SelectedSBPResponseDTO responseDTO = selectedSBPService.getLatestSelectedSBP(member);
        return ResponseEntity.ok(responseDTO);
    }


    //최신 세부신체 부분 수정
    @PutMapping
    public ResponseEntity<SelectedSBPResponseDTO> updateSelectedSBP(
            @RequestBody SelectedSBPRequestDTO requestDTO,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        Member member = userDetails.getMember();
        SelectedSBPResponseDTO responseDTO = selectedSBPService.updateLatestSelectedSBP(member, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
