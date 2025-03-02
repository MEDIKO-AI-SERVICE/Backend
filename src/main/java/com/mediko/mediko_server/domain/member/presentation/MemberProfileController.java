package com.mediko.mediko_server.domain.member.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.application.MemberProfileService;
import com.mediko.mediko_server.domain.member.domain.Member;
import com.mediko.mediko_server.global.s3.UuidFileResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "member", description = "인증/인가 API")
@RestController
@RequestMapping("/api/v1/member/img")
@RequiredArgsConstructor
public class MemberProfileController {
    private final MemberProfileService memberProfileService;

    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드합니다.")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UuidFileResponseDTO> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam("file") MultipartFile file) {
        Member member = userDetails.getMember();
        UuidFileResponseDTO uploadedImage = memberProfileService.uploadImage(file, member);
        return ResponseEntity.ok(uploadedImage);
    }

    @Operation(summary = "프로필 이미지 조회", description = "사용자의 프로필 이미지를 조회합니다.")
    @GetMapping
    public ResponseEntity<UuidFileResponseDTO> getProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        UuidFileResponseDTO profileImage = memberProfileService.getProfileImage(member);
        return ResponseEntity.ok(profileImage);
    }

    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 삭제합니다.")
    @DeleteMapping
    public ResponseEntity<Void> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        Member member = userDetails.getMember();
        memberProfileService.deleteImage(member);
        return ResponseEntity.noContent().build();
    }
}
