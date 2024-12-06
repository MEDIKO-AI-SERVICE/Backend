package com.mediko.mediko_server.domain.member.presentation;

import com.mediko.mediko_server.domain.member.application.CustomUserDetails;
import com.mediko.mediko_server.domain.member.application.MemberService;
import com.mediko.mediko_server.domain.member.dto.request.*;
import com.mediko.mediko_server.domain.member.dto.response.BasicInfoResponseDTO;
import com.mediko.mediko_server.domain.member.dto.response.HealthInfoResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static org.apache.catalina.util.XMLWriter.NO_CONTENT;
import static org.springframework.http.HttpStatus.CREATED;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/member")
public class MemberController {
    private final MemberService memberService;

    // 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(
            @RequestBody SignUpRequestDTO signUpRequestDTO) {
        memberService.signUp(signUpRequestDTO);
        return ResponseEntity.status(CREATED).build();
    }

    @PostMapping("/sign-in")
    public ResponseEntity<TokenDTO> signIn(
            @RequestBody SignInRequestDTO signInRequestDTO) {
        TokenDTO tokenDTO = memberService.signIn(signInRequestDTO.getLoginId(), signInRequestDTO.getPassword());
        return ResponseEntity.ok(tokenDTO);
    }

    // 로그아웃
    @PostMapping("/sign-out")
    public ResponseEntity<Void> logout(
            HttpServletRequest request, HttpServletResponse response) {
        memberService.signOut(request, response);
        return ResponseEntity.ok().build();
    }

    // 회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteAccount(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            HttpServletRequest request, HttpServletResponse response) {
        String loginId = customUserDetails.getUsername();
        memberService.deleteAccount(loginId, request, response);
        return ResponseEntity.ok().build();

    }

    // 닉네임 변경
    @PatchMapping("/nickname")
    public ResponseEntity<Void> updateUserNickName(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @RequestBody Map<String, String> requestBody) {
        String loginId = customUserDetails.getUsername();
        String nickname = requestBody.get("nickname");
        memberService.updateUserNickName(loginId, nickname);
        return ResponseEntity.ok().build();
    }
}
