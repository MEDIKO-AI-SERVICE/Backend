package com.mediko.mediko_server.domain.member.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.application.MemberProfileService;
import com.mediko.mediko_server.domain.member.application.MemberService;
import com.mediko.mediko_server.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "member", description = "인증/인가 API")
@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberProfileController {
    private final MemberProfileService memberProfileService;

    @Operation(summary = "프로필 이미지 업로드", description = "사용자의 프로필 이미지를 업로드합니다.")
    @PostMapping("/profile-image")
    public ResponseEntity<Void> uploadProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetail,
            @RequestPart("image") MultipartFile imageFile
    ) {
        Member member = userDetail.getMember();
        memberProfileService.uploadProfileImage(member, imageFile);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "프로필 이미지 조회", description = "사용자의 프로필 이미지 URL을 조회합니다.")
    @GetMapping("/profile-image")
    public ResponseEntity<String> getProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetail
    ) {
        Member member = userDetail.getMember();
        String imageUrl = memberProfileService.getProfileImageUrl(member);
        return ResponseEntity.ok(imageUrl);
    }

    @Operation(summary = "프로필 이미지 삭제", description = "사용자의 프로필 이미지를 삭제합니다.")
    @DeleteMapping("/profile-image")
    public ResponseEntity<Void> deleteProfileImage(
            @AuthenticationPrincipal CustomUserDetails userDetail
    ) {
        Member member = userDetail.getMember();
        memberProfileService.deleteProfileImage(member);
        return ResponseEntity.ok().build();
    }
}