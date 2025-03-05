package com.mediko.mediko_server.domain.openai.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.domain.openai.application.SelectedSignService;
import com.mediko.mediko_server.domain.openai.dto.request.SelectedSignRequestDTO;
import com.mediko.mediko_server.domain.openai.dto.response.SelectedSignResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "detailed sign", description = "상세 증상 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/selected-sign")
public class SelectedSignController {

    private final SelectedSignService selectedSignService;

    @Operation(summary = "선택한 상세 증상 저장", description = "사용자가 선택한 상세 증상을 저장합니다.")
    @PostMapping("/{selectedSBPId}")
    public ResponseEntity<SelectedSignResponseDTO> saveSelectedSign(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestBody SelectedSignRequestDTO requestDTO,
            @PathVariable(name = "selectedSBPId") Long selectedSBPId) {
        Member member = userDetail.getMember();
        SelectedSignResponseDTO responseDTO = selectedSignService.saveSelectedSign(
                member, requestDTO, selectedSBPId);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "선택한 상세 증상 조회", description = "선택된 상세 증상을 조회합니다.")
    @GetMapping("/{selectedSignId}")
    public ResponseEntity<SelectedSignResponseDTO> getSelectedSign(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @PathVariable(name = "selectedSignId") Long selectedSignId) {
        Member member = userDetail.getMember();
        SelectedSignResponseDTO responseDTO = selectedSignService.getSelectedSign(selectedSignId, member);

        return ResponseEntity.ok(responseDTO);
    }

    @Operation(summary = "선택한 상세 증상 수정", description = "선택된 상세 증상을 수정합니다.")
    @PutMapping("/{selectedSignId}")
    public ResponseEntity<SelectedSignResponseDTO> updateSelectedSign(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestBody SelectedSignRequestDTO requestDTO,
            @PathVariable(name = "selectedSignId") Long selectedSignId) {
        Member member = userDetail.getMember();
        SelectedSignResponseDTO responseDTO = selectedSignService.updateSelectedSign(
                requestDTO, selectedSignId, member);

        return ResponseEntity.ok(responseDTO);
    }
}
